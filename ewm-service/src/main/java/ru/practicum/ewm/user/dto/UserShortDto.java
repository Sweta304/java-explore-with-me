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
public class UserShortDto {
    @NotNull
    private Long id;
    @NotNull
    private String name;
}