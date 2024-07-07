package com.sunsett.debt_reminder.exceptions;

public class DebtNotFoundException extends RuntimeException {
    public DebtNotFoundException(String message) {
        super(message);
    }
}
