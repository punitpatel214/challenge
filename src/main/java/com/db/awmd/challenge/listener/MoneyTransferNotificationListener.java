package com.db.awmd.challenge.listener;

import com.db.awmd.challenge.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MoneyTransferNotificationListener implements MoneyTransferListener {

    private final NotificationService notificationService;
    private final MessageGenerator creditMessageGenerator;
    private final MessageGenerator debitMessageGenerator;

    @Autowired
    public MoneyTransferNotificationListener(NotificationService notificationService, @Qualifier("creditMessageGenerator") MessageGenerator creditMessageGenerator,
                                             @Qualifier("debitMessageGenerator") MessageGenerator debitMessageGenerator) {
        this.notificationService = notificationService;
        this.creditMessageGenerator = creditMessageGenerator;
        this.debitMessageGenerator = debitMessageGenerator;
    }

    @Override
    public void onMoneyTransfer(MoneyTransferEvent moneyTransferEvent) {
        notificationService.notifyAboutTransfer(moneyTransferEvent.getFromAccount(), debitMessageGenerator.generate(moneyTransferEvent));
        notificationService.notifyAboutTransfer(moneyTransferEvent.getToAccount(), creditMessageGenerator.generate(moneyTransferEvent));
    }

}
