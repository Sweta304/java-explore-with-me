package ru.practicum.ewm.controllers.priv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.exceptions.ForbiddenException;
import ru.practicum.ewm.exceptions.UserNotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestsService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users/{userId}/requests")
public class PrivateRequestsController {
    private final RequestsService requestsService;

    @Autowired
    public PrivateRequestsController(RequestsService requestsService) {
        this.requestsService = requestsService;
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllUserRequests(@PathVariable @Positive Long userId) throws UserNotFoundException {
        log.info("Получение всех запросов на участие пользователя с id {}", userId);
        return requestsService.getAllUserRequests(userId);
    }

    @PostMapping
    public ParticipationRequestDto addRequest(@PathVariable @Positive Long userId,
                                              @RequestParam @Positive Long eventId) throws UserNotFoundException, ForbiddenException, EventNotFoundException {
        log.info("Добавление пользователем с id {} нового запроса на участие в мероприятии {}", userId, eventId);
        return requestsService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long requestId) throws UserNotFoundException, ForbiddenException, EventNotFoundException {
        log.info("Отмена пользователем с id {} запроса {} на участие в мероприятии", userId, requestId);
        return requestsService.cancelRequest(userId, requestId);
    }
}
