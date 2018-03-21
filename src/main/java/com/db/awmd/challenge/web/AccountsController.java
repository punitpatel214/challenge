package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.MoneyTransferRequest;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.service.AccountsService;

import javax.validation.Valid;

import com.db.awmd.challenge.service.MoneyTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ConcurrentModificationException;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

    private final AccountsService accountsService;
    private final MoneyTransferService moneyTransferService;

    @Autowired
    public AccountsController(AccountsService accountsService, MoneyTransferService moneyTransferService) {
        this.accountsService = accountsService;
        this.moneyTransferService = moneyTransferService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
        log.info("Creating account {}", account);
        this.accountsService.createAccount(account);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/{accountId}")
    public Account getAccount(@PathVariable String accountId) {
        log.info("Retrieving account for id {}", accountId);
        return this.accountsService.getAccount(accountId);
    }

    @PostMapping(path = "/transferMoney")
    public ResponseEntity<String> transferMoney(@RequestBody @Valid MoneyTransferRequest moneyTransferRequest) {
        moneyTransferService.transfer(moneyTransferRequest);
        return ResponseEntity.ok("Money Transfer Successfully");
    }

}
