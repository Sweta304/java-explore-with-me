package ru.practicum.ewm.user.service;


import ru.practicum.ewm.user.EmailException;
import ru.practicum.ewm.user.UserAlreadyExistsException;
import ru.practicum.ewm.user.UserNotFoundException;
import ru.practicum.ewm.user.ValidationException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;


public interface UserService {

    UserDto addUser(NewUserRequest user) throws UserAlreadyExistsException, ValidationException, UserNotFoundException, EmailException;

    List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long id) throws UserNotFoundException;
}
