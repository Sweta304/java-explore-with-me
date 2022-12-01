package ru.practicum.ewm.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exceptions.EmailException;
import ru.practicum.ewm.exceptions.UserAlreadyExistsException;
import ru.practicum.ewm.exceptions.UserNotFoundException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/admin/users")
public class AdminUserController {
    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto addUser(@RequestBody @Valid NewUserRequest user) throws UserAlreadyExistsException, ValidationException, UserNotFoundException, EmailException {
        return userService.addUser(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                                     @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return userService.getAllUsers(ids, from, size);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable @Positive Long id) throws UserNotFoundException {
        userService.deleteUser(id);
    }
}
