package com.sunsett.debt_reminder.model.entities;

import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
public class InvoiceDebt extends Debt {
}
