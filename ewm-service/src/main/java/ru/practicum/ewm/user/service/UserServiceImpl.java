package ru.practicum.ewm.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventJpaRepository;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.UserNotFoundException;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.rating.repository.RatingJpaRepository;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserJpaRepository;
import ru.practicum.ewm.utils.MyPageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.ewm.user.UserMapper.fromUserRequestDto;
import static ru.practicum.ewm.user.UserMapper.toUserDto;
import static ru.practicum.ewm.utils.Constants.SORT_BY_ID;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;
    private final EventJpaRepository eventJpaRepository;
    private final RatingJpaRepository ratingJpaRepository;


    @Autowired
    public UserServiceImpl(UserJpaRepository userRepository, EventJpaRepository eventJpaRepository, RatingJpaRepository ratingJpaRepository) {
        this.userRepository = userRepository;
        this.eventJpaRepository = eventJpaRepository;
        this.ratingJpaRepository = ratingJpaRepository;
    }

    @Override
    public UserDto addUser(NewUserRequest userDto) throws ConflictException {
        User user = fromUserRequestDto(userDto);
        checkUniqueUserName(userDto.getName());
        user = userRepository.save(user);
        return makeUserDto(user, getUserRating(user.getId()));
    }

    @Override
    public List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = new MyPageable(from, size, SORT_BY_ID);
        Page<User> requestPage = userRepository.findAllByIdIn(ids, page);
        List<User> users = requestPage.getContent();
        Map<Long, Long> usersRatings = getUsersRatings(users);
        return users
                .stream()
                .map(x -> makeUserDto(x, usersRatings.get(x)))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("пользователь с id " + id + " не существует", "в списке пользователей не существует запрошенного пользователя"));
        userRepository.delete(user);
    }

    private void checkUniqueUserName(String name) throws ConflictException {
        if (userRepository.findByName(name) != null) {
            throw new ConflictException("Некорректное имя пользователя", "Пользователь с таким именем уже существует");
        }
    }

    private UserDto makeUserDto(User user, Long rating) {
        UserDto userDto = toUserDto(user);
        userDto.setRating(rating);
        return userDto;
    }

    private Long getUserRating(Long userId) {
        List<Event> userEvents = eventJpaRepository.findByInitiatorId(userId);
        List<Long> eventsIds = userEvents.stream().map(Event::getId).collect(Collectors.toList());
        List<Rating> ratings = ratingJpaRepository.findByEventIdIn(eventsIds);
        Long likes = ratings.stream().filter(Rating::getLiked).count();
        Long dislikes = ratings.stream().filter(Rating::getDisliked).count();
        Long rating = 0L;
        if (!userEvents.isEmpty()) {
            rating = (likes - dislikes) / userEvents.size();
        }
        return rating;
    }

    private Map<Long, Long> getUsersRatings(List<User> users) {
        Map<Long, List<Rating>> usersRatings = new HashMap<>();
        List<Long> userIds = users.stream().map(x -> x.getId()).collect(Collectors.toList());
        List<Rating> ratings = ratingJpaRepository.findByEventInitiatorIdIn(userIds);
        userIds.forEach(x -> usersRatings.put(x, ratings.stream().filter(y -> y.getEvent().getInitiator().getId().equals(x)).collect(Collectors.toList())));
        Map<Long, Long> avgRatings = new HashMap<>();
        userIds.forEach(x -> avgRatings.put(x, countRating(usersRatings.get(x))));
        return avgRatings;
    }

    private Long getUserEventsLikes(List<Rating> rating) {
        return rating.stream().filter(Rating::getLiked).count();
    }

    private Long getUserEventsDislikes(List<Rating> rating) {
        return rating.stream().filter(Rating::getDisliked).count();
    }

    private Long countRating(List<Rating> ratings) {
        Long rating = 0L;
        if (!ratings.isEmpty()) {
            rating = (getUserEventsLikes(ratings) - getUserEventsDislikes(ratings)) / ratings.size();
        }
        return rating;
    }
}
