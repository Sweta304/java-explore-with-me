package ru.practicum.ewm.controllers.priv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventRequest;
import ru.practicum.ewm.event.service.EventsService;
import ru.practicum.ewm.exceptions.CategoryNotFoundException;
import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.exceptions.ForbiddenException;
import ru.practicum.ewm.exceptions.UserNotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventsController {
    private final EventsService eventsService;

    @Autowired
    public PrivateEventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @GetMapping
    public List<EventShortDto> getAllUserEvents(@PathVariable @Positive Long userId,
                                                @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(required = false, defaultValue = "10") @Positive Integer size) throws UserNotFoundException {
        log.info("Получение всех событий пользователя с id {}", userId);
        return eventsService.getAllEventsByUserId(userId, from, size);
    }

    @PatchMapping
    public EventFullDto updateEventByUser(@PathVariable @Positive Long userId, @RequestBody @Valid UpdateEventRequest updateEventRequest) throws UserNotFoundException, EventNotFoundException, ForbiddenException, CategoryNotFoundException {
        log.info("Внесение изменений в событие с id {}", updateEventRequest.getEventId());
        return eventsService.updateEventByUser(userId, updateEventRequest);
    }

    @PostMapping
    public EventFullDto updateEventByUser(@PathVariable @Positive Long userId, @RequestBody @Valid NewEventDto newEventDto) throws UserNotFoundException, CategoryNotFoundException {
        log.info("Создание нового события пользователем с id {}", userId);
        return eventsService.addEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUser(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        log.info("Получение события по id {}", eventId);
        return eventsService.getEventByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto rejectEventByUser(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        log.info("Отмена события с id {}", eventId);
        return eventsService.rejectEventByUser(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        log.info("Получение запросов пользователя с id {} на участие в мероприятии с id {}", userId, eventId);
        return eventsService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmEventRequest(@PathVariable @Positive Long userId,
                                                       @PathVariable @Positive Long eventId,
                                                       @PathVariable @Positive Long reqId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        log.info("Подтверждение запроса с id {} на участие в мероприятии с id {}", reqId, eventId);
        return eventsService.confirmEventRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectEventRequest(@PathVariable @Positive Long userId,
                                                      @PathVariable @Positive Long eventId,
                                                      @PathVariable @Positive Long reqId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        log.info("Отклонение запроса с id {} на участие в мероприятии с id {}", reqId, eventId);
        return eventsService.rejectEventRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/like")
    public EventFullDto addLike(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        log.info("Пользователь с id {} ставит лайк событию с id {}", userId, eventId);
        return eventsService.addLike(userId, eventId);
    }

    @PatchMapping("/{eventId}/dislike")
    public EventFullDto addDislike(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        log.info("Пользователь с id {} ставит дизлайк событию с id {}", userId, eventId);
        return eventsService.addDislike(userId, eventId);
    }
}
