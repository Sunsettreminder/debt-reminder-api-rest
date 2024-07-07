package com.sunsett.debt_reminder.mapper;

import com.sunsett.debt_reminder.model.dto.DebtRequestDTO;
import com.sunsett.debt_reminder.model.dto.DebtResponseDTO;
import com.sunsett.debt_reminder.model.entities.Debt;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DebtMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Debt convertToEntity(DebtRequestDTO debtRequestDTO) {
        return modelMapper.map(debtRequestDTO, Debt.class);
    }

    public DebtResponseDTO convertToDto(Debt debt) {
        return modelMapper.map(debt, DebtResponseDTO.class);
    }
}
