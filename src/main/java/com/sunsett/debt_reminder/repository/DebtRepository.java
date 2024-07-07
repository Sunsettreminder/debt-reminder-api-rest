package com.sunsett.debt_reminder.repository;

import com.sunsett.debt_reminder.model.entities.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByUserId(Long userId);
    Optional<Debt> findByDocumentNumberAndUserId(String documentNumber, Long userId);
}
