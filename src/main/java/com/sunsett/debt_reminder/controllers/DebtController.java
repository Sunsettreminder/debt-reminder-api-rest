package com.sunsett.debt_reminder.controllers;

import com.sunsett.debt_reminder.model.dto.DebtRequestDTO;
import com.sunsett.debt_reminder.model.dto.DebtResponseDTO;
import com.sunsett.debt_reminder.services.DebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/debts")
public class DebtController {

    @Autowired
    private DebtService debtService;


    @PostMapping("/user/{userId}")
    public ResponseEntity<List<DebtResponseDTO>> createDebt(@PathVariable Long userId, @RequestBody DebtRequestDTO debtRequestDTO) {
        System.out.println("Recibida solicitud de creación de deuda");
        List<DebtResponseDTO> savedDebts = debtService.saveDebt(userId, debtRequestDTO);
        return ResponseEntity.ok(savedDebts);
    }

    @GetMapping("/user/{userId}/month")
    public ResponseEntity<List<DebtResponseDTO>> getDebtsForMonth(
            @PathVariable Long userId,
            @RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        List<DebtResponseDTO> debts = debtService.getDebtsForMonth(userId, month);
        return ResponseEntity.ok(debts);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DebtResponseDTO>> getDebtsByUserId(@PathVariable Long userId) {
        List<DebtResponseDTO> debts = debtService.getDebtsByUserId(userId);
        return ResponseEntity.ok(debts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebtResponseDTO> getDebtById(@PathVariable Long id) {
        DebtResponseDTO debt = debtService.getDebtById(id);
        return ResponseEntity.ok(debt);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DebtResponseDTO> updateDebt(@PathVariable Long id, @RequestBody DebtRequestDTO debtRequestDTO) {
        DebtResponseDTO updatedDebt = debtService.updateDebt(id, debtRequestDTO);
        return ResponseEntity.ok(updatedDebt);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDebt(@PathVariable Long id) {
        debtService.deleteDebt(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/mark-as-paid")
    public ResponseEntity<DebtResponseDTO> markDebtAsPaid(@PathVariable Long id) {
        DebtResponseDTO updatedDebt = debtService.markAsPaid(id);
        return ResponseEntity.ok(updatedDebt);
    }

    @GetMapping("/user/{userId}/unpaid-overdue")
    public ResponseEntity<List<DebtResponseDTO>> getUnpaidOverdueDebts(@PathVariable Long userId) {
        List<DebtResponseDTO> debts = debtService.getUnpaidOverdueDebts(userId);
        return ResponseEntity.ok(debts);
    }

    @GetMapping("/user/{userId}/current-week")
    public ResponseEntity<List<DebtResponseDTO>> getDebtsForCurrentWeek(@PathVariable Long userId) {
        List<DebtResponseDTO> debts = debtService.getDebtsForCurrentWeek(userId);
        return ResponseEntity.ok(debts);
    }

    @GetMapping("/user/{userId}/paid")
    public ResponseEntity<List<DebtResponseDTO>> getPaidDebts(@PathVariable Long userId) {
        List<DebtResponseDTO> debts = debtService.getPaidDebts(userId);
        return ResponseEntity.ok(debts);
    }

    @GetMapping("/user/{userId}/future")
    public ResponseEntity<List<DebtResponseDTO>> getFutureDebts(@PathVariable Long userId) {
        List<DebtResponseDTO> debts = debtService.getFutureDebts(userId);
        return ResponseEntity.ok(debts);
    }

    @GetMapping("/user/{userId}/unpaid")
    public ResponseEntity<List<DebtResponseDTO>> getUnpaidDebts(@PathVariable Long userId) {
        List<DebtResponseDTO> debts = debtService.getUnpaidDebts(userId);
        return ResponseEntity.ok(debts);
    }
}
