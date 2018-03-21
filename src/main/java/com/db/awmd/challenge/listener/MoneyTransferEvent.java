package com.db.awmd.challenge.listener;

import com.db.awmd.challenge.domain.Account;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@EqualsAndHashCode
public class MoneyTransferEvent {
    private Account fromAccount;
    private Account toAccount;
    private BigDecimal transferAmount;
    private LocalDate transferTime;
}
