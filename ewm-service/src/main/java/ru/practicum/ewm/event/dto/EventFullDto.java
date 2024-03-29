package ru.practicum.ewm.event.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.dictionary.EventStates;
import ru.practicum.ewm.user.dto.UserShortDto;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
@EqualsAndHashCode
public class EventFullDto {
    @NotNull
    private String annotation;
    @NotNull
    private CategoryDto category;
    private Long confirmedRequests;
    private String createdOn;
    private String description;
    @NotNull
    private String eventDate;
    private Long id;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Location location;
    @NotNull
    private Boolean paid;
    private Long participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private EventStates state;
    @NotNull
    private String title;
    private Long views;
    private Long rating;
    private Long likes;
    private Long dislikes;

}
