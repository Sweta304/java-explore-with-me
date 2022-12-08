package ru.practicum.ewm.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dictionary.RequestStates;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventJpaRepository;
import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.exceptions.ForbiddenException;
import ru.practicum.ewm.exceptions.UserNotFoundException;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestsJpaRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.dictionary.EventStates.PUBLISHED;
import static ru.practicum.ewm.dictionary.RequestStates.CONFIRMED;
import static ru.practicum.ewm.request.RequestMapper.toParticipationRequestDto;


@Service
@Slf4j
public class RequestsServiceImpl implements RequestsService {

    private final EventJpaRepository eventJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final RequestsJpaRepository requestsJpaRepository;

    @Autowired
    public RequestsServiceImpl(EventJpaRepository eventJpaRepository,
                               UserJpaRepository userJpaRepository,
                               RequestsJpaRepository requestsJpaRepository) {
        this.eventJpaRepository = eventJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.requestsJpaRepository = requestsJpaRepository;
    }

    @Override
    public List<ParticipationRequestDto> getAllUserRequests(Long userId) throws UserNotFoundException {
        checkUser(userId);
        List<Request> requests = requestsJpaRepository.findByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) throws ForbiddenException, UserNotFoundException, EventNotFoundException {
        checkUser(userId);
        checkEvent(eventId);
        User user = userJpaRepository.findById(userId).get();
        Event event = eventJpaRepository.findById(eventId).get();
        if (requestsJpaRepository.findByEventIdAndRequesterId(eventId, userId).isPresent()) {
            throw new ForbiddenException("Запрещено добавление запроса на участие в событии " + eventId, "Запрос на участие в событии от данного пользователя уже зарегистрирован");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Запрещено добавление запроса на участие в событии " + eventId, "Пользователь является организатором события");
        }
        if (!event.getEventState().equals(PUBLISHED)) {
            throw new ForbiddenException("Запрещено добавление запроса на участие в событии " + eventId, "Запрашиваемое событие не опубликовано");
        }
        if (event.getParticipantLimit().equals(getConfirmedRequestsQty(eventId))) {
            throw new ForbiddenException("Запрещено добавление запроса на участие в событии " + eventId, "Регистрация заявок на участие в событии окончена");
        }
        Request request = new Request();
        request.setRequester(user);
        if (event.getRequestModeration()) {
            request.setStatus(RequestStates.PENDING);
        } else {
            request.setStatus(CONFIRMED);
        }
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        request = requestsJpaRepository.save(request);
        return toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) throws UserNotFoundException, EventNotFoundException {
        checkUser(userId);
        Request request = requestsJpaRepository.findById(requestId).orElseThrow(() -> new EventNotFoundException("Запроса не существует", "Запрос не найден в списке запросов"));
        request.setStatus(RequestStates.CANCELED);
        return toParticipationRequestDto(requestsJpaRepository.save(request));
    }

    private void checkUser(Long id) throws UserNotFoundException {
        if (userJpaRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException("Пользователя не существует", "Пользователь с id " + id + " не найден");
        }
    }

    private void checkEvent(Long id) throws EventNotFoundException {
        if (eventJpaRepository.findById(id).isEmpty()) {
            throw new EventNotFoundException("События с id " + id + " не существует", "Событие не найдено в таблице");
        }
    }

    private void checkInitiator(Long userId, Long eventId) throws ForbiddenException {
        if (!eventJpaRepository.findById(eventId).get().getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Невозможно редактировать событие", "Инициатором события является другой пользователь");
        }
    }

    private int getConfirmedRequestsQty(Long eventId) {
        List<Request> requests = requestsJpaRepository.findByEventIdAndStatus(eventId, CONFIRMED);
        return requests.size();
    }
}
