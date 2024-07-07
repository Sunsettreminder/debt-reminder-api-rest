package com.sunsett.debt_reminder.services;

import com.sunsett.debt_reminder.model.dto.UserAuthDTO;
import com.sunsett.debt_reminder.model.dto.UserRequestDTO;
import com.sunsett.debt_reminder.model.entities.User;
import com.sunsett.debt_reminder.repository.UserRepository;
import com.sunsett.debt_reminder.exceptions.UserNotFoundException;
import com.sunsett.debt_reminder.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public User registerUser(UserRequestDTO userRequestDTO) {
        User user = userMapper.convertToEntity(userRequestDTO);
        return userRepository.save(user);
    }

    public User authenticateUser(UserAuthDTO userAuthDTO) {
        User user = userRepository.findByUserEmail(userAuthDTO.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        if (!user.getPassword().equals(userAuthDTO.getPassword())) {
            throw new UserNotFoundException("Contraseña incorrecta");
        }
        return user;
    }
}
