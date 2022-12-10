package ru.practicum.ewm.user.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
@EqualsAndHashCode
public class UserDto {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String email;
    private Double rating;

}
