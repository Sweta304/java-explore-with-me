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
public class UpdateEventRequest {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    @NotNull
    private Long eventId;
    private Boolean paid;
    private Integer participantLimit;
    private String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateEventRequest that = (UpdateEventRequest) o;
        return Objects.equals(annotation, that.annotation) && Objects.equals(category, that.category) && Objects.equals(description, that.description) && Objects.equals(eventDate, that.eventDate) && Objects.equals(eventId, that.eventId) && Objects.equals(paid, that.paid) && Objects.equals(participantLimit, that.participantLimit) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, category, description, eventDate, eventId, paid, participantLimit, title);
    }
}
