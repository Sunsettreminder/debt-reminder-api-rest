package com.sunsett.debt_reminder.controllers;

import com.sunsett.debt_reminder.model.dto.NotificationResponseDTO;
import com.sunsett.debt_reminder.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}/today")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsForToday(@PathVariable Long userId) {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsForToday(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/send-due-today")
    public ResponseEntity<Void> sendNotificationsForDueDebtsToday() {
        notificationService.sendNotificationsForDueDebtsToday();
        return ResponseEntity.ok().build();
    }
}
