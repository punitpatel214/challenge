package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.MoneyTransferRequest;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.listener.MoneyTransferListener;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class MoneyTransferServiceTest {

    @Test(expected = InsufficientBalanceException.class)
    public void shouldThrowInSufficientBalanceExceptionOnTransferAmountLargerThanBalance() {
        Account fromAccount = new Account("180011001");
        fromAccount.setBalance(BigDecimal.valueOf(5000));

        Account toAccount = new Account("180022001");
        toAccount.setBalance(BigDecimal.valueOf(2500));

        AccountsRepository accountsRepository = new AccountsRepositoryInMemory();
        accountsRepository.createAccount(fromAccount);
        accountsRepository.createAccount(toAccount);
        MoneyTransferService moneyTransferService = new MoneyTransferService(accountsRepository, Collections.emptyList());

        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest(fromAccount.getAccountId(), toAccount.getAccountId(), BigDecimal.valueOf(6000));
        moneyTransferService.transfer(moneyTransferRequest);
    }

    @Test
    public void shouldTransferMoneyToBeneficiaryAccount() {
        Account fromAccount = new Account("190011001");
        fromAccount.setBalance(BigDecimal.valueOf(5000));

        Account toAccount = new Account("190022001");
        toAccount.setBalance(BigDecimal.valueOf(11000));

        AccountsRepository accountsRepository = new AccountsRepositoryInMemory();
        accountsRepository.createAccount(fromAccount);
        accountsRepository.createAccount(toAccount);

        MoneyTransferService moneyTransferService = new MoneyTransferService(accountsRepository, Collections.emptyList());

        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest(fromAccount.getAccountId(), toAccount.getAccountId(), BigDecimal.valueOf(2000));
        moneyTransferService.transfer(moneyTransferRequest);

        assertThat(fromAccount.getBalance(), is(BigDecimal.valueOf(3000)));
        assertThat(toAccount.getBalance(), is(BigDecimal.valueOf(13000)));

    }

    @Test
    public void shouldInvokeMoneyTransferListenerOnCompleteTransferMoney() {
        Account fromAccount = new Account("1800180011");
        fromAccount.setBalance(BigDecimal.valueOf(5000));

        Account toAccount = new Account("1800123428");
        toAccount.setBalance(BigDecimal.valueOf(11000));

        AccountsRepository accountsRepository = new AccountsRepositoryInMemory();
        accountsRepository.createAccount(fromAccount);
        accountsRepository.createAccount(toAccount);

        MoneyTransferListener moneyTransferListener = Mockito.mock(MoneyTransferListener.class);
        MoneyTransferService moneyTransferService = new MoneyTransferService(accountsRepository, Collections.singletonList(moneyTransferListener));

        MoneyTransferRequest moneyTransferRequest = new MoneyTransferRequest(fromAccount.getAccountId(), toAccount.getAccountId(), BigDecimal.valueOf(2000));
        moneyTransferService.transfer(moneyTransferRequest);

        assertThat(fromAccount.getBalance(), is(BigDecimal.valueOf(3000)));
        assertThat(toAccount.getBalance(), is(BigDecimal.valueOf(13000)));

        Mockito.verify(moneyTransferListener, Mockito.times(1)).onMoneyTransfer(Mockito.any());
    }


}