package com.sunsett.debt_reminder.security;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
@Component
public class TokenBlackList {
    private final Set<String> blacklist = new HashSet<>();

    public void addToken(String token) {
        blacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }
}