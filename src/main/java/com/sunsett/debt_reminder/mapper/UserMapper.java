package com.sunsett.debt_reminder.mapper;

import com.sunsett.debt_reminder.model.dto.UserRequestDTO;
import com.sunsett.debt_reminder.model.entities.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    public User convertToEntity(UserRequestDTO userRequestDTO) {
        return modelMapper.map(userRequestDTO, User.class);
    }

    public UserRequestDTO convertToDTO(User user) {
        return modelMapper.map(user, UserRequestDTO.class);
    }
}
