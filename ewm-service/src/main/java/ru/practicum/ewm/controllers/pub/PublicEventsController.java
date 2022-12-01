package ru.practicum.ewm.controllers.pub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventsService;
import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.exceptions.IncorrectEventParamsException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/events")
public class PublicEventsController {
    private final EventsService eventsService;

    @Autowired
    public PublicEventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @GetMapping
    public List<EventShortDto> getAllEvents(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(required = false) Boolean onlyAvailable,
                                            @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
                                            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) throws IncorrectEventParamsException {
        log.info("Выполняется поиск событий по параметрам:\n" +
                "аннотация или описание содержит - {}\n" +
                "категории - {}\n" +
                "начало события после - {}\n" +
                "конец события до - {}\n", text, categories, rangeStart, rangeEnd);
        return eventsService.getAllPublicEventsByFilter(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("{eventId}")
    public EventFullDto getEvent(@PathVariable Long eventId) throws EventNotFoundException {
        log.info("Получение информации о событии {}", eventId);
        return eventsService.getEvent(eventId);
    }
}
