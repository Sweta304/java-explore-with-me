package ru.practicum.ewm.user;


import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .rating(user.getUserRating())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto
                .builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User fromUserShortDto(UserShortDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        return user;
    }

    public static User fromUserRequestDto(NewUserRequest userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}
