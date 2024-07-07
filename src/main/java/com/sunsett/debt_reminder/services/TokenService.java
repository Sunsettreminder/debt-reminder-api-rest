package com.sunsett.debt_reminder.services;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenService {
    private Set<String> invalidatedTokens = new HashSet<>();

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    public boolean isTokenInvalid(String token) {
        return invalidatedTokens.contains(token);
    }
}
