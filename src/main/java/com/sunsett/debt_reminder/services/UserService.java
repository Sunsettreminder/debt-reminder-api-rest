package com.sunsett.debt_reminder.services;

import com.sunsett.debt_reminder.model.entities.User;
import com.sunsett.debt_reminder.repository.UserRepository;
import com.sunsett.debt_reminder.security.JwtService;
import com.sunsett.debt_reminder.security.LoginRequest;
import com.sunsett.debt_reminder.security.TokenBlackList;
import com.sunsett.debt_reminder.security.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenBlackList tokenBlackList;

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(user, user);
        return TokenResponse.builder()
                .token(token)
                .build();
    }

    public TokenResponse addUsuario(User usuario) {
        User user = User.builder()
                .username(usuario.getUsername())
                .password(passwordEncoder.encode(usuario.getPassword()))
                .email(usuario.getEmail())
                .build();

        usuarioRepository.save(user);
        String token = jwtService.getToken(user, user);
        return TokenResponse.builder()
                .token(token)
                .build();
    }
}