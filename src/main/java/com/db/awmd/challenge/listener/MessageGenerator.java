package com.db.awmd.challenge.listener;

import java.math.BigDecimal;
import java.text.NumberFormat;

public interface MessageGenerator {
    String generate(MoneyTransferEvent moneyTransferEvent);

    default String formatCurrency(BigDecimal bigDecimal) {
        return NumberFormat.getCurrencyInstance().format(bigDecimal);
    }
}
