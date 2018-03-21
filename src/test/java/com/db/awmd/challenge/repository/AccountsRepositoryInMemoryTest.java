package com.db.awmd.challenge.repository;
import com.db.awmd.challenge.domain.Account;

import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.TryLockFailException;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class AccountsRepositoryInMemoryTest {

    @Test
    public void shouldTransferAmountFromOneAccountToOther() {
        AccountsRepository accountsRepository = new AccountsRepositoryInMemory();
        Account fromAccount = new Account("18001010101");
        fromAccount.setBalance(BigDecimal.valueOf(345000));

        Account toAccount = new Account("1800211126");
        toAccount.setBalance(BigDecimal.valueOf(26000));

        accountsRepository.transferAmount(fromAccount, toAccount, BigDecimal.valueOf(10000));
        assertThat(fromAccount.getBalance(), is(BigDecimal.valueOf(335000)));
        assertThat(toAccount.getBalance(), is(BigDecimal.valueOf(36000)));
    }

    @Test(expected = InsufficientBalanceException.class)
    public void shouldThrowInsufficientBalanceWhenBalanceLessThanTransferAmount() {
        AccountsRepository accountsRepository = new AccountsRepositoryInMemory();
        Account fromAccount = new Account("18001010101");
        fromAccount.setBalance(BigDecimal.valueOf(25000));

        Account toAccount = new Account("1800211126");
        accountsRepository.transferAmount(fromAccount, toAccount, BigDecimal.valueOf(25000.50));
    }

    @Test(expected = TryLockFailException.class)
    public void shouldThrowMoneyTransferFailExceptionWhenLockNotAcquireOnFromAccount() {
        AccountsRepository accountsRepository = new AccountsRepositoryInMemory();
        Account fromAccount = new Account("21031801110") {
            @Override
            public Lock getLock() {
                return new ReentrantLock() {
                    @Override
                    public boolean tryLock(long timeout, TimeUnit unit) {
                        return false;
                    }
                };
            }
        };
        fromAccount.setBalance(BigDecimal.valueOf(25000));

        Account toAccount = new Account("2103201812");
        accountsRepository.transferAmount(fromAccount, toAccount, BigDecimal.valueOf(10000));
    }

    @Test(expected = TryLockFailException.class)
    public void shouldThrowMoneyTransferFailExceptionWhenLockNotAcquireOnToAccount() {
        AccountsRepository accountsRepository = new AccountsRepositoryInMemory();
        Account toAccount = new Account("21031801110") {
            @Override
            public Lock getLock() {
                return new ReentrantLock() {
                    @Override
                    public boolean tryLock(long timeout, TimeUnit unit) {
                        return false;
                    }
                };
            }
        };
        accountsRepository.transferAmount(new Account("2103101813"), toAccount, BigDecimal.valueOf(10000));
    }




}