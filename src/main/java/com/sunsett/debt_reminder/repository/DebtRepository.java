package com.sunsett.debt_reminder.repository;

import com.sunsett.debt_reminder.model.entities.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByUserId(Long userId);
    Optional<Debt> findByDocumentNumberAndUserId(String documentNumber, Long userId);
    List<Debt> findByUserIdAndDueDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<Debt> findByUserIdAndStatusFalseAndDueDateBefore(Long userId, LocalDate date);
    List<Debt> findByUserIdAndStatusTrue(Long userId);
    List<Debt> findByUserIdAndStatusFalseAndDueDateAfter(Long userId, LocalDate date);
    List<Debt> findByUserIdAndStatusFalse(Long userId);
    List<Debt> findByUserIdAndStatusFalseAndDueDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<Debt> findByDueDate(LocalDate dueDate);
}
