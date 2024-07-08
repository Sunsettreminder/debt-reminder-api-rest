package com.sunsett.debt_reminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DebtReminderApplication {
    public static void main(String[] args) {
        SpringApplication.run(DebtReminderApplication.class, args);
    }
}
