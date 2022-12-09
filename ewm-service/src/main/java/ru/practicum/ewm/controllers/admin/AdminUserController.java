package ru.practicum.ewm.controllers.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exceptions.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
public class AdminUserController {
    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto addUser(@RequestBody @Valid NewUserRequest user) throws UserAlreadyExistsException, ValidationException, UserNotFoundException, EmailException, ConflictException {
        log.info("Админ добавляет нового пользователя {}", user.getEmail());
        return userService.addUser(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                                     @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Админ получает информацию о пользователях по списку id - {}", ids);
        return userService.getAllUsers(ids, from, size);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable @Positive Long id) throws UserNotFoundException {
        log.info("Админ получает информацию о пользователе с id - {}", id);
        userService.deleteUser(id);
    }
}
