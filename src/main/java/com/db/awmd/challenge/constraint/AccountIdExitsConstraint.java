package com.db.awmd.challenge.constraint;

import com.db.awmd.challenge.validator.AccountIdExitsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AccountIdExitsValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountIdExitsConstraint {
    String message() default "Account not found";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
