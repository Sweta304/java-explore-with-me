package ru.practicum.ewm.user.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
@EqualsAndHashCode
public class NewUserRequest {
    @NotNull
    private String name;
    @NotNull
    @Email
    private String email;

}
