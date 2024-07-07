package com.sunsett.debt_reminder.services;

import com.sunsett.debt_reminder.exceptions.DebtAlreadyExistsException;
import com.sunsett.debt_reminder.exceptions.DebtNotFoundException;
import com.sunsett.debt_reminder.model.dto.DebtRequestDTO;
import com.sunsett.debt_reminder.model.dto.DebtResponseDTO;
import com.sunsett.debt_reminder.model.entities.*;
import com.sunsett.debt_reminder.repository.DebtRepository;
import com.sunsett.debt_reminder.repository.UserRepository;
import com.sunsett.debt_reminder.mapper.DebtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DebtService {

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DebtMapper debtMapper;

    public DebtResponseDTO saveDebt(Long userId, DebtRequestDTO debtRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DebtNotFoundException("Usuario no encontrado"));

        debtRepository.findByDocumentNumberAndUserId(debtRequestDTO.getDocumentNumber(), userId)
                .ifPresent(existingDebt -> {
                    throw new DebtAlreadyExistsException("Ya existe una deuda con este número de documento para este usuario");
                });

        Debt debt = createDebtInstance(debtRequestDTO);
        debt.setUser(user);
        Debt savedDebt = debtRepository.save(debt);
        return debtMapper.convertToDto(savedDebt);
    }

    private Debt createDebtInstance(DebtRequestDTO debtRequestDTO) {
        switch (debtRequestDTO.getDebtType().toUpperCase()) {
            case "INVOICE":
                return new InvoiceDebt();
            case "SERVICE":
                return new ServiceDebt();
            case "TAX":
                return new TaxDebt();
            case "INSTALLMENT":
                return new InstallmentDebt();
            default:
                throw new IllegalArgumentException("Tipo de deuda no válido: " + debtRequestDTO.getDebtType());
        }
    }

    public List<DebtResponseDTO> getDebtsByUserId(Long userId) {
        return debtRepository.findByUserId(userId).stream()
                .map(debtMapper::convertToDto)
                .collect(Collectors.toList());
    }

    public DebtResponseDTO getDebtById(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new DebtNotFoundException("Deuda no encontrada"));
        return debtMapper.convertToDto(debt);
    }

    public DebtResponseDTO updateDebt(Long debtId, DebtRequestDTO debtRequestDTO) {
        Debt existingDebt = debtRepository.findById(debtId)
                .orElseThrow(() -> new DebtNotFoundException("Deuda no encontrada"));

        existingDebt.setDocumentNumber(debtRequestDTO.getDocumentNumber());
        existingDebt.setCompany(debtRequestDTO.getCompany());
        existingDebt.setDueDate(debtRequestDTO.getDueDate());
        existingDebt.setAmount(debtRequestDTO.getAmount());
        existingDebt.setStatus(debtRequestDTO.isStatus());

        Debt updatedDebt = debtRepository.save(existingDebt);
        return debtMapper.convertToDto(updatedDebt);
    }

    public void deleteDebt(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new DebtNotFoundException("Deuda no encontrada"));
        debtRepository.delete(debt);
    }

    // Nuevo método para obtener deudas de un mes específico
    public List<DebtResponseDTO> getDebtsForMonth(Long userId, YearMonth month) {
        LocalDate startOfMonth = month.atDay(1);
        LocalDate endOfMonth = month.atEndOfMonth();

        // Obtener todas las deudas del mes y las deudas no pagadas de meses anteriores
        List<Debt> debts = debtRepository.findByUserIdAndDueDateBetweenOrStatusIsFalseAndUserId(userId, startOfMonth, endOfMonth, userId);

        return debts.stream()
                .map(debt -> {
                    DebtResponseDTO dto = debtMapper.convertToDto(debt);
                    dto.setColor(determineColor(debt));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<DebtResponseDTO> getUnpaidOverdueDebts(Long userId) {
        LocalDate today = LocalDate.now();

        List<Debt> debts = debtRepository.findByUserIdAndStatusFalseAndDueDateBefore(userId, today);
        return debts.stream()
                .map(debtMapper::convertToDto)
                .collect(Collectors.toList());
    }

    private String determineColor(Debt debt) {
        LocalDate today = LocalDate.now();
        LocalDate dueDate = debt.getDueDate();
        boolean isPaid = debt.isStatus();

        if (isPaid) {
            return "GREEN";
        } else if (dueDate.isBefore(today)) {
            return "RED";
        } else if (!dueDate.isBefore(today) && dueDate.isBefore(today.plusDays(7))) {
            return "YELLOW";
        } else {
            return "BLACK";
        }
    }

    // Nuevo método para marcar una deuda como pagada
    public DebtResponseDTO markAsPaid(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new DebtNotFoundException("Deuda no encontrada"));

        if (debt.isStatus()) {
            throw new IllegalArgumentException("La deuda ya está marcada como pagada");
        }

        debt.setStatus(true);
        Debt updatedDebt = debtRepository.save(debt);
        return debtMapper.convertToDto(updatedDebt);
    }
}
