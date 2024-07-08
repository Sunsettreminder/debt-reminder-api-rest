package com.sunsett.debt_reminder.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private String color;

    // Campos adicionales para TaxDebt
    private List<InstallmentDTO> installments;

    // Campos adicionales para InstallmentDebt
    private BigDecimal capital;
    private BigDecimal interes;
    private BigDecimal cuota;
    private int daysBetweenInstallments;

    // Campos adicionales para mostrar saldo y cuota
    private BigDecimal remainingAmount;  // Saldo restante
    private BigDecimal installmentAmount;  // Cuota

    // Getters y Setters para remainingAmount y installmentAmount
    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }
}
