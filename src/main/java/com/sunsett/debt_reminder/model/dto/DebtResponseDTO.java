package com.sunsett.debt_reminder.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebtResponseDTO {

    private long debtId;
    private String documentNumber;
    private String company;
    private LocalDate dueDate;
    private BigDecimal amount;
    private boolean status;
    private LocalDateTime createdDate;
}
