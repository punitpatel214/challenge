package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@ToString(exclude = "lock")
public class Account {

    @NotNull
    @NotEmpty
    private final String accountId;

    @NotNull
    @Min(value = 0, message = "Initial balance must be positive.")
    private BigDecimal balance;

    @JsonIgnore
    private Lock lock;

    public Account(String accountId) {
        this(accountId, BigDecimal.ZERO);
    }

    @JsonCreator
    public Account(@JsonProperty("accountId") String accountId,
                   @JsonProperty("balance") BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
        this.lock = new ReentrantLock();
    }

    public String obfuscateAccountId() {
        return String.join("", "XXXXXXXX", accountId.substring(Math.max(accountId.length() - 2, 0)));
    }
}
