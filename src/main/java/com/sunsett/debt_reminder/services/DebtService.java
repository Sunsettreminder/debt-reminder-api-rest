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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
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

    public List<DebtResponseDTO> saveDebt(Long userId, DebtRequestDTO debtRequestDTO) {
        System.out.println("Inicio de saveDebt");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DebtNotFoundException("Usuario no encontrado"));

        System.out.println("Usuario encontrado: " + user.getId());

        debtRepository.findByDocumentNumberAndUserId(debtRequestDTO.getDocumentNumber(), userId)
                .ifPresent(existingDebt -> {
                    throw new DebtAlreadyExistsException("Ya existe una deuda con este número de documento para este usuario");
                });

        List<Debt> debtsToSave = new ArrayList<>();
        Debt debt = debtMapper.convertToEntity(debtRequestDTO);
        debt.setUser(user);

        if (debt instanceof InstallmentDebt) {
            BigDecimal principal = debtRequestDTO.getAmount();
            BigDecimal annualInterestRate = debtRequestDTO.getInteres();
            int numInstallments = debtRequestDTO.getNumberOfInstallments();
            int daysBetweenInstallments = debtRequestDTO.getDaysBetweenInstallments();
            LocalDate dueDate = debtRequestDTO.getDueDate();

            // Calcular la cuota usando la fórmula del valor presente de una anualidad
            BigDecimal monthlyInterestRate = annualInterestRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
            BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyInterestRate);
            BigDecimal pow = onePlusRate.pow(numInstallments);
            BigDecimal installmentAmount = principal.multiply(monthlyInterestRate).multiply(pow)
                    .divide(pow.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

            for (int i = 0; i < numInstallments; i++) {
                BigDecimal interest = principal.multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal capital = installmentAmount.subtract(interest);

                InstallmentDebt installmentDebt = (InstallmentDebt) debtMapper.convertToEntity(debtRequestDTO);
                installmentDebt.setUser(user);
                installmentDebt.setDueDate(dueDate.plusDays(daysBetweenInstallments * i));
                installmentDebt.setCapital(capital);
                installmentDebt.setInteres(interest);
                installmentDebt.setCuota(installmentAmount);
                installmentDebt.setAmount(installmentAmount); // Set the amount to the installment amount
                installmentDebt.setDocumentNumber(debtRequestDTO.getDocumentNumber() + "-" + (i + 1));
                debtsToSave.add(installmentDebt);

                principal = principal.subtract(capital);
            }
        } else if (debt instanceof TaxDebt) {
            for (int i = 0; i < debtRequestDTO.getNumberOfInstallments(); i++) {
                TaxDebt taxDebt = (TaxDebt) debtMapper.convertToEntity(debtRequestDTO);
                taxDebt.setUser(user);
                taxDebt.setDueDate(debtRequestDTO.getDueDate().plusMonths(i));
                taxDebt.setDocumentNumber(debtRequestDTO.getDocumentNumber() + "-" + (i + 1));
                taxDebt.setAmount(debtRequestDTO.getAmount().divide(BigDecimal.valueOf(debtRequestDTO.getNumberOfInstallments()), 2, RoundingMode.HALF_UP)); // Set the amount to the installment amount
                debtsToSave.add(taxDebt);
            }
        } else {
            debtsToSave.add(debt);
        }

        List<Debt> savedDebts = debtRepository.saveAll(debtsToSave);
        return savedDebts.stream().map(debtMapper::convertToDto).collect(Collectors.toList());
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

        debtMapper.updateDebtFromDto(debtRequestDTO, existingDebt);
        Debt updatedDebt = debtRepository.save(existingDebt);
        return debtMapper.convertToDto(updatedDebt);
    }

    public void deleteDebt(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new DebtNotFoundException("Deuda no encontrada"));
        debtRepository.delete(debt);
    }

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

    public List<DebtResponseDTO> getUnpaidOverdueDebts(Long userId) {
        LocalDate today = LocalDate.now();

        List<Debt> debts = debtRepository.findByUserIdAndStatusFalseAndDueDateBefore(userId, today);
        return debts.stream()
                .map(this::mapAndDetermineColor)
                .collect(Collectors.toList());
    }

    public List<DebtResponseDTO> getDebtsForCurrentWeek(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate endOfWeek = today.plusDays(7);

        List<Debt> debts = debtRepository.findByUserIdAndStatusFalseAndDueDateBetween(userId, today, endOfWeek);
        return debts.stream()
                .map(this::mapAndDetermineColor)
                .collect(Collectors.toList());
    }

    public List<DebtResponseDTO> getPaidDebts(Long userId) {
        List<Debt> debts = debtRepository.findByUserIdAndStatusTrue(userId);
        return debts.stream()
                .map(this::mapAndDetermineColor)
                .collect(Collectors.toList());
    }

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

    public List<DebtResponseDTO> getUnpaidDebts(Long userId) {
        List<Debt> debts = debtRepository.findByUserIdAndStatusFalse(userId);
        return debts.stream()
                .map(this::mapAndDetermineColor)
                .collect(Collectors.toList());
    }

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

    public DebtResponseDTO markAsUnpaid(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new DebtNotFoundException("Deuda no encontrada"));

        if (!debt.isStatus()) {
            throw new IllegalArgumentException("La deuda ya está marcada como no pagada");
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
