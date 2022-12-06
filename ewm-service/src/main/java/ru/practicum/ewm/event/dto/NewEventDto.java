package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
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
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotNull
    private String title;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewEventDto that = (NewEventDto) o;
        return Objects.equals(annotation, that.annotation) && Objects.equals(category, that.category) && Objects.equals(description, that.description) && Objects.equals(eventDate, that.eventDate) && Objects.equals(location, that.location) && Objects.equals(paid, that.paid) && Objects.equals(participantLimit, that.participantLimit) && Objects.equals(requestModeration, that.requestModeration) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, category, description, eventDate, location, paid, participantLimit, requestModeration, title);
    }
}
