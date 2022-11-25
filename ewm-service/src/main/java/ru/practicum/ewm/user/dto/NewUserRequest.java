package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class NewUserRequest {
    private String name;
    private String email;

    public static boolean validate(NewUserRequest user) {
        boolean isValid = false;
        if (
                !(user.getEmail() == null
                        || user.getEmail().isEmpty()
                        || user.getEmail().isBlank()
                )) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean validateMail(NewUserRequest user) {
        boolean isValid = false;
        if (!user.getEmail().contains("@") && !(user.getEmail() == null)) {
            isValid = true;
        }
        return isValid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewUserRequest userDto = (NewUserRequest) o;
        return Objects.equals(name, userDto.name) && Objects.equals(email, userDto.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
