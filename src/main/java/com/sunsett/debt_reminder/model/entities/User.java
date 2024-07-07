package com.sunsett.debt_reminder.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Id de Usuario
    private long id;

    @Column(name = "user_name", nullable = false) // Nombre del usuario
    private String userName;

    @Column(name = "user_email", nullable = false) // Email del usuario
    private String userEmail;

    @Column(name = "password", nullable = false) // Contraseña del usuario
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // Lista de deudas del usuario
    private List<Debt> debts;
}
