package com.sunsett.debt_reminder.repository;

import com.sunsett.debt_reminder.model.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDebt_UserIdAndNotificationDateBetween(long userId, LocalDateTime startDate, LocalDateTime endDate);
}
