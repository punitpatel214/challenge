package com.db.awmd.challenge.listener;

import java.util.EventListener;

public interface MoneyTransferListener extends EventListener {
    void onMoneyTransfer(MoneyTransferEvent moneyTransferEvent);
}
