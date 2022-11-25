package ru.practicum.ewm.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.user.EmailException;
import ru.practicum.ewm.user.UserNotFoundException;
import ru.practicum.ewm.user.ValidationException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserJpaRepository;
import utils.MyPageable;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.user.UserMapper.fromUserRequestDto;
import static ru.practicum.ewm.user.UserMapper.toUserDto;
import static ru.practicum.ewm.user.dto.NewUserRequest.validateMail;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;

    @Autowired
    public UserServiceImpl(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto addUser(NewUserRequest userDto) throws ValidationException, EmailException {
        User user = fromUserRequestDto(userDto);
        if (!NewUserRequest.validate(userDto)) {
            log.error("валидация пользователя не пройдена");
            throw new ValidationException("ошибка данных пользователя", "проверьте корректность данных");
        } else if (validateMail(userDto)) {
            throw new EmailException("некорректный Email");
        }
        return toUserDto(userRepository.save(user));
    }

    public List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = new MyPageable(from, size, sort);
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
}
