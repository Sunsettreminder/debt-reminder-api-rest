package com.sunsett.debt_reminder.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tax_debt")
public class TaxDebt extends Debt {

    @ElementCollection
    @CollectionTable(name = "tax_debt_installments", joinColumns = @JoinColumn(name = "debt_id"))
    private List<Installment> installments;
}
