package com.db.awmd.challenge.domain;

import com.db.awmd.challenge.constraint.AccountIdExitsConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class MoneyTransferRequest {

    @NotBlank
    @NotNull
    @AccountIdExitsConstraint(message = "From Account not exits")
    private String fromAccountId;

    @NotBlank
    @NotNull
    @AccountIdExitsConstraint(message = "To Account not exits")
    private String toAccountId;

    @NotNull
    @Min(value = 1, message = "Transfer amount should be positive non-zero amount")
    private BigDecimal transferAmount;

    public MoneyTransferRequest(String fromAccountId, String toAccountId, BigDecimal transferAmount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.transferAmount = transferAmount;
    }
}
