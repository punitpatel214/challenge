package com.db.awmd.challenge.validator;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class AccountIdExitsValidatorTest {

    @Test
    public void testAccountIdExitsValidation() {
        AccountsRepository accountsRepository = Mockito.mock(AccountsRepository.class);
        AccountIdExitsValidator accountIdExitsValidator = new AccountIdExitsValidator(accountsRepository);

        Mockito.when(accountsRepository.getAccount("1001")).thenReturn(null);
        assertThat(accountIdExitsValidator.isValid("1001", Mockito.any()), is(false));

        Mockito.when(accountsRepository.getAccount("1002")).thenReturn(new Account("1002"));
        assertThat(accountIdExitsValidator.isValid("1002", Mockito.any()), is(true));
    }
}