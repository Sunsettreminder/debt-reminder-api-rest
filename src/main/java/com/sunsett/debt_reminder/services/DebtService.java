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
import org.springframework.context.ApplicationEventPublisher;
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

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public DebtResponseDTO saveDebt(Long userId, DebtRequestDTO debtRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DebtNotFoundException("Usuario no encontrado"));

        debtRepository.findByDocumentNumberAndUserId(debtRequestDTO.getDocumentNumber(), userId)
                .ifPresent(existingDebt -> {
                    throw new DebtAlreadyExistsException("Ya existe una deuda con este número de documento para este usuario");
                });

        Debt debt = debtMapper.convertToEntity(debtRequestDTO);
        debt.setUser(user);
        Debt savedDebt = debtRepository.save(debt);
        eventPublisher.publishEvent(savedDebt);
        return mapAndDetermineColor(savedDebt);
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
                .map(this::mapAndDetermineColor)
                .collect(Collectors.toList());
    }

    public DebtResponseDTO getDebtById(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new DebtNotFoundException("Deuda no encontrada"));
        DebtResponseDTO dto = debtMapper.convertToDto(debt);
        dto.setColor(determineColor(debt)); // Asegúrate de que el color se determine y asigne aquí
        return dto;
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
        eventPublisher.publishEvent(updatedDebt);
        return mapAndDetermineColor(updatedDebt);
    }

    public void deleteDebt(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new DebtNotFoundException("Deuda no encontrada"));
        debtRepository.delete(debt);
        eventPublisher.publishEvent(debt);
    }

    // Nuevo método para obtener deudas de un mes específico
    public List<DebtResponseDTO> getDebtsForMonth(Long userId, YearMonth month) {
        LocalDate startOfMonth = month.atDay(1);
        LocalDate endOfMonth = month.atEndOfMonth();

        List<Debt> debts = debtRepository.findByUserIdAndDueDateBetween(userId, startOfMonth, endOfMonth);

        return debts.stream()
                .map(debt -> {
                    DebtResponseDTO dto = debtMapper.convertToDto(debt);
                    dto.setColor(determineColor(debt));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Deudas no pagadas y vencidas (Rojas)
    public List<DebtResponseDTO> getUnpaidOverdueDebts(Long userId) {
        LocalDate today = LocalDate.now();

        List<Debt> debts = debtRepository.findByUserIdAndStatusFalseAndDueDateBefore(userId, today);
        return debts.stream()
                .map(this::mapAndDetermineColor)
                .collect(Collectors.toList());
    }

    // Deudas de la semana actual (Amarillas)
    public List<DebtResponseDTO> getDebtsForCurrentWeek(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate endOfWeek = today.plusDays(7);

        List<Debt> debts = debtRepository.findByUserIdAndStatusFalseAndDueDateBetween(userId, today, endOfWeek);
        return debts.stream()
                .map(this::mapAndDetermineColor)
                .collect(Collectors.toList());
    }

    // Deudas pagadas (Verdes)
    public List<DebtResponseDTO> getPaidDebts(Long userId) {
        List<Debt> debts = debtRepository.findByUserIdAndStatusTrue(userId);
        return debts.stream()
                .map(this::mapAndDetermineColor)
                .collect(Collectors.toList());
    }

    // Deudas futuras (Negras)
    public List<DebtResponseDTO> getFutureDebts(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate endOfWeek = today.plusDays(7);

        List<Debt> allFutureDebts = debtRepository.findByUserIdAndStatusFalseAndDueDateAfter(userId, today);
        List<Debt> futureDebtsExcludingCurrentWeek = allFutureDebts.stream()
                .filter(debt -> debt.getDueDate().isAfter(endOfWeek))
                .collect(Collectors.toList());

        return futureDebtsExcludingCurrentWeek.stream()
                .map(this::mapAndDetermineColor)
                .collect(Collectors.toList());
    }

    // Deudas no Pagadas (antiguas, actuales, futuras)
    public List<DebtResponseDTO> getUnpaidDebts(Long userId) {
        List<Debt> debts = debtRepository.findByUserIdAndStatusFalse(userId);
        return debts.stream()
                .map(this::mapAndDetermineColor)
                .collect(Collectors.toList());
    }

    // Método para marcar una deuda como pagada
    public DebtResponseDTO markAsPaid(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new DebtNotFoundException("Deuda no encontrada"));

        if (debt.isStatus()) {
            throw new IllegalArgumentException("La deuda ya está marcada como pagada");
        }

        debt.setStatus(true);
        Debt updatedDebt = debtRepository.save(debt);
        return mapAndDetermineColor(updatedDebt);
    }

    // Método para marcar una deuda como no pagada
    public DebtResponseDTO markAsUnpaid(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new DebtNotFoundException("Deuda no encontrada"));

        if (debt.isStatus()) {
            throw new IllegalArgumentException("La deuda ya está marcada como pagada");
        }

        debt.setStatus(false);
        Debt updatedDebt = debtRepository.save(debt);
        return mapAndDetermineColor(updatedDebt);
    }

    private DebtResponseDTO mapAndDetermineColor(Debt debt) {
        DebtResponseDTO debtResponseDTO = debtMapper.convertToDto(debt);
        debtResponseDTO.setColor(determineColor(debt));
        return debtResponseDTO;
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
}
