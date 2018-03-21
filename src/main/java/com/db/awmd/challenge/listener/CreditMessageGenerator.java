package com.db.awmd.challenge.listener;

import com.db.awmd.challenge.domain.Account;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Qualifier("creditMessageGenerator")
public class CreditMessageGenerator implements MessageGenerator {

    @Override
    public String generate(MoneyTransferEvent moneyTransferEvent) {
        Account toAccount = moneyTransferEvent.getToAccount();
        String messageTemplate = "%s is credited to your A/c No %s on %s By Transfer. Available Balance is %s";
        return String.format(messageTemplate, formatCurrency(moneyTransferEvent.getTransferAmount()), toAccount.obfuscateAccountId(),
                LocalDate.now(), formatCurrency(toAccount.getBalance()));
    }


}
