package com.sunsett.debt_reminder.mapper;

import com.sunsett.debt_reminder.model.dto.DebtRequestDTO;
import com.sunsett.debt_reminder.model.dto.DebtResponseDTO;
import com.sunsett.debt_reminder.model.dto.InstallmentDTO;
import com.sunsett.debt_reminder.model.entities.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DebtMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Debt convertToEntity(DebtRequestDTO debtRequestDTO) {
        if (debtRequestDTO == null) {
            throw new IllegalArgumentException("DebtRequestDTO cannot be null");
        }

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
                throw new IllegalArgumentException("Invalid debt type: " + debtRequestDTO.getDebtType());
        }
        System.out.println("Debt convertido: " + debt);
        return debt;
    }

    public DebtResponseDTO convertToDto(Debt debt) {
        if (debt == null) {
            throw new IllegalArgumentException("Debt cannot be null");
        }

        DebtResponseDTO debtResponseDTO = modelMapper.map(debt, DebtResponseDTO.class);
        debtResponseDTO.setColor(determineColor(debt));
        if (debt instanceof TaxDebt) {
            if (((TaxDebt) debt).getInstallments() != null) {
                debtResponseDTO.setInstallments(modelMapper.map(((TaxDebt) debt).getInstallments(), new org.modelmapper.TypeToken<List<InstallmentDTO>>() {}.getType()));
            } else {
                System.out.println("Instalments are null for debt: " + debt);
            }
        }
        System.out.println("DebtResponseDTO convertido: " + debtResponseDTO);
        return debtResponseDTO;
    }

    public void updateDebtFromDto(DebtRequestDTO debtRequestDTO, Debt debt) {
        if (debtRequestDTO == null || debt == null) {
            throw new IllegalArgumentException("DebtRequestDTO and Debt cannot be null");
        }

        modelMapper.map(debtRequestDTO, debt);
        if (debt instanceof TaxDebt) {
            if (debtRequestDTO.getInstallments() != null) {
                ((TaxDebt) debt).setInstallments(modelMapper.map(debtRequestDTO.getInstallments(), new org.modelmapper.TypeToken<List<Installment>>() {}.getType()));
            } else {
                System.out.println("Instalments in DebtRequestDTO are null: " + debtRequestDTO);
            }
        }
        System.out.println("Debt actualizado desde DTO: " + debt);
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
