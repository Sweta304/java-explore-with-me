package ru.practicum.ewm.event.service;


import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.exceptions.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;


public interface EventsService {


    List<EventFullDto> getAllEventsByFilter(List<Long> users,
                                            List<String> states,
                                            List<Long> categories,
                                            String rangeStart,
                                            String rangeEnd,
                                            Integer from,
                                            Integer size) throws IncorrectEventStateException;

    EventFullDto editEventByAdmin(Long eventId, AdminUpdateEventRequest event) throws EventNotFoundException, CategoryNotFoundException;

    EventFullDto publishEvent(Long eventId) throws EventNotFoundException, IncorrectEventParamsException;

    EventFullDto rejectEvent(Long eventId) throws EventNotFoundException, IncorrectEventParamsException;

    List<EventShortDto> getAllPublicEventsByFilter(String text,
                                                   List<Long> categories,
                                                   Boolean paid,
                                                   String rangeStart,
                                                   String rangeEnd,
                                                   Boolean onlyAvailable,
                                                   String sort,
                                                   Integer from,
                                                   Integer size,
                                                   String ip,
                                                   String uri) throws IncorrectEventParamsException;

    EventFullDto getEvent(Long eventId, String ip, String uri) throws EventNotFoundException;

    List<EventShortDto> getAllEventsByUserId(Long userId, Integer from, Integer size) throws UserNotFoundException;

    EventFullDto updateEventByUser(Long userId, UpdateEventRequest updateEventRequest) throws UserNotFoundException, EventNotFoundException, ForbiddenException, CategoryNotFoundException;

    EventFullDto addEvent(Long userId, NewEventDto newEventDto) throws UserNotFoundException, CategoryNotFoundException;

    EventFullDto getEventByUser(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException;

    EventFullDto rejectEventByUser(Long userId, Long eventId) throws ForbiddenException, UserNotFoundException, EventNotFoundException;

    List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException;

    ParticipationRequestDto confirmEventRequest(Long userId, Long eventId, Long reqId) throws UserNotFoundException, EventNotFoundException, ForbiddenException;

    ParticipationRequestDto rejectEventRequest(Long userId, Long eventId, Long reqId) throws UserNotFoundException, EventNotFoundException, ForbiddenException;

    EventFullDto addLike(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException;

    EventFullDto addDislike(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException;
}
