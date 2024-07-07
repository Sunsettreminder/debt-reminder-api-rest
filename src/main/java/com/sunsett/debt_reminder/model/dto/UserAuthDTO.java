package com.sunsett.debt_reminder.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDTO {
    @NotBlank(message = "El email del usuario no puede estar vacío")
    private String userEmail;

    @NotBlank(message = "La contraseña del usuario no puede estar en blanco")
    private String password;
}
