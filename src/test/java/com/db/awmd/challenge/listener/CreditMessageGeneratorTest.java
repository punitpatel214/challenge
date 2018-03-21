package com.db.awmd.challenge.listener;

import com.db.awmd.challenge.domain.Account;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CreditMessageGeneratorTest {

    @Test
    public void testGeneratedMessageOfUKLocale() {
        Locale.setDefault(Locale.UK);
        Account toAccount = new Account("1234500214");
        toAccount.setBalance(BigDecimal.valueOf(26041.45));

        LocalDate currentDate = LocalDate.now();
        MessageGenerator messageGenerator = new CreditMessageGenerator();
        String generatedMessage = messageGenerator.generate(MoneyTransferEvent.builder()
                .toAccount(toAccount)
                .transferAmount(BigDecimal.valueOf(5000))
                .transferTime(currentDate)
                .build());

        assertThat(generatedMessage, equalTo("£5,000.00 is credited to your A/c No XXXXXXXX14 on " + currentDate + " By Transfer. Available Balance is £26,041.45"));
    }

    @Test
    public void testGeneratedMessageOfUSLocale() {
        Locale.setDefault(Locale.US);
        Account toAccount = new Account("9898015437");
        toAccount.setBalance(BigDecimal.valueOf(2604.5));

        LocalDate currentDate = LocalDate.now();
        MessageGenerator messageGenerator = new CreditMessageGenerator();
        String generatedMessage = messageGenerator.generate(MoneyTransferEvent.builder()
                .toAccount(toAccount)
                .transferAmount(BigDecimal.valueOf(300))
                .transferTime(currentDate)
                .build());

        assertThat(generatedMessage, is("$300.00 is credited to your A/c No XXXXXXXX37 on " + currentDate + " By Transfer. Available Balance is $2,604.50"));
    }
}