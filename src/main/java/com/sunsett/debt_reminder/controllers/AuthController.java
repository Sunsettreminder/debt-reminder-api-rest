package com.sunsett.debt_reminder.controllers;

import com.sunsett.debt_reminder.model.dto.UserAuthDTO;
import com.sunsett.debt_reminder.model.dto.UserRequestDTO;
import com.sunsett.debt_reminder.model.entities.User;
import com.sunsett.debt_reminder.services.TokenService;
import com.sunsett.debt_reminder.services.UserService;
import com.sunsett.debt_reminder.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
        User newUser = userService.registerUser(userRequestDTO);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody UserAuthDTO userAuthDTO) {
        User authenticatedUser = userService.authenticateUser(userAuthDTO);
        String token = jwtUtil.generateToken(authenticatedUser.getUserEmail());
        return ResponseEntity.ok().header("Authorization", "Bearer " + token).body(authenticatedUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        tokenService.invalidateToken(actualToken);
        return ResponseEntity.ok("User logged out successfully");
    }
}
