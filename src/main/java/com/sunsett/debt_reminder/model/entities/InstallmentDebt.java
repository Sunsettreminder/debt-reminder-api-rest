package com.sunsett.debt_reminder.model.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("INSTALLMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentDebt extends Debt {
    private BigDecimal capital;
    private BigDecimal interes;
    private BigDecimal cuota;
    private int daysBetweenInstallments; // Días entre cuotas
}
