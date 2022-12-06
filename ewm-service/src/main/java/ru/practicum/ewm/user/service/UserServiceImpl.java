package ru.practicum.ewm.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.EmailException;
import ru.practicum.ewm.exceptions.UserNotFoundException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserJpaRepository;
import ru.practicum.ewm.utils.MyPageable;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.user.UserMapper.fromUserRequestDto;
import static ru.practicum.ewm.user.UserMapper.toUserDto;
import static ru.practicum.ewm.user.dto.NewUserRequest.validateMail;
import static ru.practicum.ewm.utils.Constants.SORT_BY_ID;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;

    @Autowired
    public UserServiceImpl(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto addUser(NewUserRequest userDto) throws ValidationException, EmailException, ConflictException {
        User user = fromUserRequestDto(userDto);
        checkUniqueUserName(userDto.getName());
        if (!NewUserRequest.validate(userDto)) {
            log.error("валидация пользователя не пройдена");
            throw new ValidationException("ошибка данных пользователя", "проверьте корректность данных");
        } else if (validateMail(userDto)) {
            throw new EmailException("некорректный Email");
        }
        return toUserDto(userRepository.save(user));
    }

    public List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = new MyPageable(from, size, SORT_BY_ID);
        Page<User> requestPage = userRepository.findAllByList(ids, page);
        return requestPage.getContent()
                .stream()
                .map(x -> toUserDto(x))
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("пользователь с id " + id + " не существует", "в списке пользователей не существует запрошенного пользователя"));
        userRepository.delete(user);
    }

    private void checkUniqueUserName(String name) throws ConflictException {
        if (userRepository.findByName(name) != null) {
            throw new ConflictException("Некорректное имя пользователя", "Пользователь с таким именем уже существует");
        }
    }
}
