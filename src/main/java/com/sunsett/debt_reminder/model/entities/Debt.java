package com.sunsett.debt_reminder.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "debts", uniqueConstraints = @UniqueConstraint(columnNames = {"document_number", "user_id"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Debt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Id de deuda
    private long debtId;

    @Column(name = "document_number", nullable = false) // Num. documento de deuda
    private String documentNumber;

    @Column(name = "company", nullable = false) // Empresa de la deuda
    private String company;

    @Column(name = "due_date", nullable = false) // Fecha de vencimiento de la deuda
    private LocalDate dueDate;

    @Column(name = "created_date", nullable = false) // Fecha de creación de la deuda
    private LocalDateTime createdDate;

    @Column(name = "amount", nullable = false) // Monto de la deuda
    private BigDecimal amount;

    @Column(name = "status", nullable = false) // Estado de la deuda
    private boolean status; // true si esta Pagada, false si esta Pendiente

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Usuario de la deuda
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
}
