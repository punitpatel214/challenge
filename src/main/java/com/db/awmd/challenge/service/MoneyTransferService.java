package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.MoneyTransferRequest;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.listener.MoneyTransferEvent;
import com.db.awmd.challenge.listener.MoneyTransferListener;
import com.db.awmd.challenge.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MoneyTransferService {

    private final AccountsRepository accountsRepository;
    private final List<MoneyTransferListener> moneyTransferListeners;

    @Autowired
    public MoneyTransferService(AccountsRepository accountsRepository, List<MoneyTransferListener> moneyTransferListeners) {
        this.accountsRepository = accountsRepository;
        this.moneyTransferListeners = moneyTransferListeners;
    }

    public void transfer(MoneyTransferRequest moneyTransferRequest) {
        Account fromAccount = accountsRepository.getAccount(moneyTransferRequest.getFromAccountId());
        BigDecimal transferAmount = moneyTransferRequest.getTransferAmount();

        if (transferAmount.compareTo(fromAccount.getBalance()) > 0) {
            throw new InsufficientBalanceException("Insufficient Balance for transfer");
        }

        Account toAccount = accountsRepository.getAccount(moneyTransferRequest.getToAccountId());
        accountsRepository.transferAmount(fromAccount, toAccount, transferAmount);

        notifyListener(moneyTransferRequest, fromAccount, toAccount);
    }

    private void notifyListener(MoneyTransferRequest moneyTransferRequest, Account fromAccount, Account toAccount) {
        MoneyTransferEvent moneyTransferEvent = MoneyTransferEvent.builder()
                .fromAccount(fromAccount)
                .transferAmount(moneyTransferRequest.getTransferAmount())
                .toAccount(toAccount)
                .transferTime(LocalDate.now())
                .build();
        moneyTransferListeners.forEach(moneyTransferListener -> moneyTransferListener.onMoneyTransfer(moneyTransferEvent));
    }
}
