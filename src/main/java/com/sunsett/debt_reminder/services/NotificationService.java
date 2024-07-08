package com.sunsett.debt_reminder.services;

import com.sunsett.debt_reminder.model.dto.NotificationResponseDTO;
import com.sunsett.debt_reminder.model.entities.*;
import com.sunsett.debt_reminder.repository.DebtRepository;
import com.sunsett.debt_reminder.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private DebtRepository debtRepository;

    public List<NotificationResponseDTO> getNotificationsForToday(long userId) {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);

        List<Notification> notifications = notificationRepository.findByDebt_UserIdAndNotificationDateBetween(userId,
                todayStart, todayEnd);

        return notifications.stream()
                .map(notification -> new NotificationResponseDTO(notification.getId(), notification.getMessage(),
                        notification.getNotificationDate()))
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 0 * * ?") // Esta cron expression se ejecuta a medianoche todos los días
    public void sendNotificationsForDueDebtsToday() {
        LocalDate today = LocalDate.now();
        List<Debt> dueDebts = debtRepository.findByDueDate(today);

        dueDebts.forEach(this::createNotificationForDebt);
    }

    public void createNotificationForDebt(Debt debt) {
        if (debt.getUser() == null) {
            return; // Si no hay usuario asociado, no crear notificación
        }

        String debtType = getDebtType(debt);
        String message = debt.getDocumentNumber() + " - Tu " + debtType + " con " + debt.getCompany() + " de " +
                debt.getAmount() + " vence hoy!, Apresurate en pagarla!";

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setNotificationDate(LocalDateTime.now());
        notification.setDebt(debt);

        notificationRepository.save(notification);
    }

    private String getDebtType(Debt debt) {
        if (debt instanceof InstallmentDebt) {
            return "Deuda a plazos";
        } else if (debt instanceof TaxDebt) {
            return "Deuda de impuestos";
        } else if (debt instanceof ServiceDebt) {
            return "Deuda de servicio";
        } else if (debt instanceof InvoiceDebt) {
            return "Deuda de factura";
        } else {
            return "Deuda desconocida"; // Manejo de errores
        }
    }
}
