package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MockNotificationService implements NotificationService {

    @Override
    public void notifyAboutTransfer(Account account, String transferDescription) {
        log.info("Received transfer notification request for account {} and notification message - {}", account.getAccountId(), transferDescription);
    }
}
