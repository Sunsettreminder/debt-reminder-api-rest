package com.sunsett.debt_reminder.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebtRequestDTO {

    @NotBlank(message = "El tipo de deuda no puede estar vacío")
    private String debtType;

    @NotBlank(message = "El número de documento no puede estar vacío")
    private String documentNumber;

    @NotBlank(message = "La compañía no puede estar vacía")
    private String company;

    @NotNull(message = "La fecha de vencimiento no puede estar vacía")
    private LocalDate dueDate;

    @NotNull(message = "El monto no puede estar vacío")
    private BigDecimal amount;

    @NotNull(message = "El estado no puede estar vacío")
    private boolean status;
}
