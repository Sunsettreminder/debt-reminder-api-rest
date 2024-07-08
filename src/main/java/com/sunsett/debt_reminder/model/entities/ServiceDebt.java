package com.sunsett.debt_reminder.model.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;


@Entity
@DiscriminatorValue("SERVICE")
@Data

public class ServiceDebt extends Debt {
    // No campos adicionales necesarios según tu descripción.
}
