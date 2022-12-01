package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Integer views;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventShortDto that = (EventShortDto) o;
        return Objects.equals(id, that.id) && Objects.equals(annotation, that.annotation) && Objects.equals(category, that.category) && Objects.equals(confirmedRequests, that.confirmedRequests) && Objects.equals(eventDate, that.eventDate) && Objects.equals(initiator, that.initiator) && Objects.equals(paid, that.paid) && Objects.equals(title, that.title) && Objects.equals(views, that.views);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, annotation, category, confirmedRequests, eventDate, initiator, paid, title, views);
    }
}
