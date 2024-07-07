package com.sunsett.debt_reminder.exceptions;

public class DebtAlreadyExistsException extends RuntimeException {
    public DebtAlreadyExistsException(String message) {
        super(message);
    }
}
