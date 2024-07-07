package com.sunsett.debt_reminder.events;

import com.sunsett.debt_reminder.model.entities.Debt;
import com.sunsett.debt_reminder.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DebtEventListener {

    @Autowired
    private NotificationService notificationService;

    @EventListener
    public void handleDebtSaveOrUpdate(Debt debt) {
        if (debt.getDueDate().isEqual(LocalDate.now())) {
            notificationService.createNotificationForDebt(debt);
        }
    }
}
