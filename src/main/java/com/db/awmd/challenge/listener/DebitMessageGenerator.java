package com.db.awmd.challenge.listener;

import com.db.awmd.challenge.domain.Account;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("debitMessageGenerator")
public class DebitMessageGenerator implements MessageGenerator {

    @Override
    public String generate(MoneyTransferEvent moneyTransferEvent) {
        Account fromAccount = moneyTransferEvent.getFromAccount();
        String messageTemplate = "%s is debited from your A/c No %s on %s By Transfer. Available Balance is %s";
        return String.format(messageTemplate, formatCurrency(moneyTransferEvent.getTransferAmount()), fromAccount.obfuscateAccountId(),
                moneyTransferEvent.getTransferTime(), formatCurrency(fromAccount.getBalance()));
    }
}
