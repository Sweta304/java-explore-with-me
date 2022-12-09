package ru.practicum.ewm.event.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
@EqualsAndHashCode
public class NewEventDto {
    @NotNull
    private String annotation;
    @NotNull
    private Long category;
    @NotNull
    private String description;
    @NotNull
    private String eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    @NotNull
    private String title;

}
