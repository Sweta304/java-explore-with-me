package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.dictionary.EventStates;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
public class EventFullDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private EventStates eventState;
    private String title;
    private Integer views;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventFullDto that = (EventFullDto) o;
        return Objects.equals(id, that.id) && Objects.equals(annotation, that.annotation) && Objects.equals(category, that.category) && Objects.equals(confirmedRequests, that.confirmedRequests) && Objects.equals(createdOn, that.createdOn) && Objects.equals(description, that.description) && Objects.equals(eventDate, that.eventDate) && Objects.equals(initiator, that.initiator) && Objects.equals(location, that.location) && Objects.equals(paid, that.paid) && Objects.equals(participantLimit, that.participantLimit) && Objects.equals(publishedOn, that.publishedOn) && Objects.equals(requestModeration, that.requestModeration) && eventState == that.eventState && Objects.equals(title, that.title) && Objects.equals(views, that.views);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, annotation, category, confirmedRequests, createdOn, description, eventDate, initiator, location, paid, participantLimit, publishedOn, requestModeration, eventState, title, views);
    }
}
