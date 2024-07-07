package com.sunsett.debt_reminder.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Id de la notificación
    private long id;

    @Column(name = "message", nullable = false) // Mensaje de la notificación
    private String message;

    @Column(name = "notification_date", nullable = false) // Fecha de la notificación
    private LocalDateTime notificationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debt_id", nullable = false) // Deuda asociada
    private Debt debt;
}
