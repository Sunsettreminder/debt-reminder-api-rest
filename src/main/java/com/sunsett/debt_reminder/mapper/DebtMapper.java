package com.sunsett.debt_reminder.mapper;

import com.sunsett.debt_reminder.model.dto.DebtResponseDTO;
import com.sunsett.debt_reminder.model.dto.DebtRequestDTO;
import com.sunsett.debt_reminder.model.entities.*;
import com.sunsett.debt_reminder.model.entities.Debt;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DebtMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Debt convertToEntity(DebtRequestDTO debtRequestDTO) {
        Debt debt;
        switch (debtRequestDTO.getDebtType().toUpperCase()) {
            case "INVOICE":
                debt = modelMapper.map(debtRequestDTO, InvoiceDebt.class);
                break;
            case "SERVICE":
                debt = modelMapper.map(debtRequestDTO, ServiceDebt.class);
                break;
            case "TAX":
                debt = modelMapper.map(debtRequestDTO, TaxDebt.class);
                break;
            case "INSTALLMENT":
                debt = modelMapper.map(debtRequestDTO, InstallmentDebt.class);
                break;
            default:
                throw new IllegalArgumentException("Tipo de deuda no válido: " + debtRequestDTO.getDebtType());
        }
        return debt;
    }

    public DebtResponseDTO convertToDto(Debt debt) {
        DebtResponseDTO debtResponseDTO = modelMapper.map(debt, DebtResponseDTO.class);
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
