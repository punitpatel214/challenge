package com.db.awmd.challenge.validator;

import com.db.awmd.challenge.constraint.AccountIdExitsConstraint;
import com.db.awmd.challenge.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class AccountIdExitsValidator implements ConstraintValidator<AccountIdExitsConstraint, String> {

    private final AccountsRepository accountsRepository;

    @Autowired
    public AccountIdExitsValidator(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    @Override
    public void initialize(AccountIdExitsConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(String accountId, ConstraintValidatorContext context) {
        return accountId != null && accountsRepository.getAccount(accountId) != null;
    }
}
