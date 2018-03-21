package com.db.awmd.challenge.listener;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.NotificationService;
import org.junit.Test;
import org.mockito.Mockito;

public class MoneyTransferNotificationListenerTest {

    @Test
    public void testCreditNotification() {
        NotificationService notificationService = Mockito.mock(NotificationService.class);
        MessageGenerator debitMessageGenerator = Mockito.mock(MessageGenerator.class);
        MessageGenerator creditMessageGenerator = Mockito.mock(MessageGenerator.class);
        MoneyTransferListener moneyTransferListener = new MoneyTransferNotificationListener(notificationService, creditMessageGenerator, debitMessageGenerator);

        Account toAccount = new Account("1234500214");
        Account fromAccount = new Account("1234505318");
        MoneyTransferEvent moneyTransferEvent = MoneyTransferEvent.builder()
                .toAccount(toAccount)
                .fromAccount(fromAccount)
                .build();
        moneyTransferListener.onMoneyTransfer(moneyTransferEvent);

        Mockito.verify(notificationService, Mockito.times(1)).notifyAboutTransfer(toAccount, creditMessageGenerator.generate(moneyTransferEvent));
        Mockito.verify(notificationService, Mockito.times(1)).notifyAboutTransfer(fromAccount, debitMessageGenerator.generate(moneyTransferEvent));
    }


}