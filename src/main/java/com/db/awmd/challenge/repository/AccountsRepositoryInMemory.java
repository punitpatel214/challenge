package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.TryLockFailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Value("${lock.timeout.seconds:2}")
    private int tryLockTimeOutInSecond;

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

    @Override
    public void transferAmount(Account fromAccount, Account toAccount, BigDecimal amount) {
        executeInLock(fromAccount, () -> executeInLock(toAccount, () -> transferAmountAsync(fromAccount,toAccount,amount)));
    }

    private void executeInLock(Account account, Runnable command) {
        try {
            boolean lockAcquired = account.getLock().tryLock(tryLockTimeOutInSecond, TimeUnit.SECONDS);
            if (!lockAcquired) {
                throw new TryLockFailException("Other Transaction for same account already in Process, Please try after some time");
            }
            try {
                command.run();
            } finally {
                account.getLock().unlock();
            }
        } catch (InterruptedException e) {
            throw new TryLockFailException(e);
        }
    }

    private void transferAmountAsync(Account fromAccount, Account toAccount, BigDecimal amount) {
        if (amount.compareTo(fromAccount.getBalance()) > 0) {
            throw new InsufficientBalanceException("Insufficient Balance in account for transfer");
        }
        log.info("Transferring Amount {} from Account {} to Account {}", amount, fromAccount.getAccountId(), toAccount.getAccountId());
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
    }

}
