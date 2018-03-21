package com.db.awmd.challenge.listener;

import com.db.awmd.challenge.domain.Account;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class DebitMessageGeneratorTest {

    @Test
    public void testGeneratedMessageOfCanadaLocale() {
        Locale.setDefault(Locale.CANADA);
        Account fromAccount = new Account("1800131085");
        fromAccount.setBalance(BigDecimal.valueOf(156041.45));

        LocalDate currentDate = LocalDate.now();
        MessageGenerator messageGenerator = new DebitMessageGenerator();
        String generatedMessage = messageGenerator.generate(MoneyTransferEvent.builder()
                .fromAccount(fromAccount)
                .transferAmount(BigDecimal.valueOf(19500))
                .transferTime(currentDate)
                .build());

        assertThat(generatedMessage, is(equalTo("$19,500.00 is debited from your A/c No XXXXXXXX85 on " + currentDate + " By Transfer. Available Balance is $156,041.45")));
    }

    @Test
    public void testGeneratedMessageOfUSLocale() {
        Locale.setDefault(Locale.UK);
        Account fromAccount = new Account("2809250388");
        fromAccount.setBalance(BigDecimal.valueOf(250025.45));

        LocalDate currentDate = LocalDate.now();
        MessageGenerator messageGenerator = new DebitMessageGenerator();
        String generatedMessage = messageGenerator.generate(MoneyTransferEvent.builder()
                .fromAccount(fromAccount)
                .transferAmount(BigDecimal.valueOf(2500))
                .transferTime(currentDate)
                .build());

        assertThat(generatedMessage, is("£2,500.00 is debited from your A/c No XXXXXXXX88 on " + currentDate + " By Transfer. Available Balance is £250,025.45"));
    }
}