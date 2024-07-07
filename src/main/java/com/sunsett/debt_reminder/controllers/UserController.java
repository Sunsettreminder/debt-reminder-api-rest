package com.sunsett.debt_reminder.controllers;

import com.sunsett.debt_reminder.model.entities.User;
import com.sunsett.debt_reminder.security.LoginRequest;
import com.sunsett.debt_reminder.security.TokenBlackList;
import com.sunsett.debt_reminder.security.TokenResponse;
import com.sunsett.debt_reminder.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*") // Permite solicitudes de cualquier origen
@RequiredArgsConstructor
public class UserController {
    private final UserService usuarioService;
    private final TokenBlackList tokenBlackList;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse token = usuarioService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> registrar(@RequestBody User usuario) {
        usuarioService.addUsuario(usuario);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlackList.addToken(token);
        }
        return ResponseEntity.ok("Sesión cerrada");
    }

    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndpoint() {
        return ResponseEntity.ok("Hola, estás autenticado");
    }
}
