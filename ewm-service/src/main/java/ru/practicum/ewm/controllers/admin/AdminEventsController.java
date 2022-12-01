package ru.practicum.ewm.controllers.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.service.EventsService;
import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.exceptions.IncorrectEventParamsException;
import ru.practicum.ewm.exceptions.IncorrectEventStateException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/events")
public class AdminEventsController {
    private final EventsService eventsService;

    @Autowired
    public AdminEventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @GetMapping
    public List<EventFullDto> getAllEvents(@RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false) List<String> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(required = false, defaultValue = "10") @Positive Integer size) throws IncorrectEventStateException {
        log.info("Админ выполняет поиск событий по параметрам:\n" +
                "пользователи - {}\n" +
                "статусы событий - {}\n" +
                "категории - {}\n" +
                "начало события после - {}\n" +
                "конец события до - {}\n", users, states, categories, rangeStart, rangeEnd);
        return eventsService.getAllEventsByFilter(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("{eventId}")
    public EventFullDto editEvent(@PathVariable Long eventId,
                                  @RequestBody AdminUpdateEventRequest event) throws EventNotFoundException {
        log.info("Админ редактирует данные события {}", eventId);
        return eventsService.editEventByAdmin(eventId, event);
    }

    @PatchMapping("{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) throws EventNotFoundException, IncorrectEventParamsException {
        log.info("Админ публикует событие {}", eventId);
        return eventsService.publishEvent(eventId);
    }

    @PatchMapping("{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) throws EventNotFoundException, IncorrectEventParamsException {
        log.info("Админ отклоняет публикацию события{}", eventId);
        return eventsService.rejectEvent(eventId);
    }
}
