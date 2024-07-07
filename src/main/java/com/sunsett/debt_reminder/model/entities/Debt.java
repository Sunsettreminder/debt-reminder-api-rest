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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long debtId;

    @Column(name = "document_number", nullable = false)
    private String documentNumber;

    @Column(name = "company", nullable = false)
    private String company;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "status", nullable = false)
    private boolean status; // true si esta Pagada, false si esta Pendiente

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
}
