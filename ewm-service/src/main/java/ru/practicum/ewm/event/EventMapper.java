package ru.practicum.ewm.event;


import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.Location;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

import static ru.practicum.ewm.category.CategoryMapper.fromCategoryDto;
import static ru.practicum.ewm.category.CategoryMapper.toCategoryDto;
import static ru.practicum.ewm.dictionary.EventStates.PENDING;
import static ru.practicum.ewm.dictionary.EventStates.PUBLISHED;
import static ru.practicum.ewm.user.UserMapper.fromUserShortDto;
import static ru.practicum.ewm.user.UserMapper.toUserShortDto;
import static ru.practicum.ewm.utils.Constants.DATE_TIME_FORMATTER;

public class EventMapper {

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto
                .builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .initiator(toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .rating(event.getRating())
                .build();
    }

    public static Event fromEventShortDto(EventShortDto eventDto) {
        Event event = new Event();
        event.setAnnotation(eventDto.getAnnotation());
        event.setCategory(fromCategoryDto(eventDto.getCategory()));
        event.setConfirmedRequests(eventDto.getConfirmedRequests());
        event.setEventDate(LocalDateTime.parse(eventDto.getEventDate(), DATE_TIME_FORMATTER));
        event.setInitiator(fromUserShortDto(eventDto.getInitiator()));
        event.setPaid(eventDto.getPaid());
        event.setDescription(eventDto.getTitle());
        event.setViews(eventDto.getViews());
        return event;
    }

    public static EventFullDto toEventFullDto(Event event) {
        String publishedOn = "";
        if (event.getEventState().equals(PUBLISHED)) {
            publishedOn = event.getPublishedOn().format(DATE_TIME_FORMATTER);
        }
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(DATE_TIME_FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .initiator(toUserShortDto(event.getInitiator()))
                .location(Location.builder()
                        .lon(event.getLon())
                        .lat(event.getLat())
                        .build())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(publishedOn)
                .requestModeration(event.getRequestModeration())
                .state(event.getEventState())
                .title(event.getTitle())
                .views(event.getViews())
                .rating(event.getRating())
                .likes(event.getLikes())
                .dislikes(event.getDislikes())
                .build();
    }

    public static Event fromNewEventDto(NewEventDto newEventDto, Category category, User userId) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMATTER));
        event.setLon(newEventDto.getLocation().getLon());
        event.setLat(newEventDto.getLocation().getLat());
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());
        event.setEventState(PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(userId);
        event.setConfirmedRequests(0);
        return event;
    }
}
