package ru.practicum.ewm.event.service;

import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryJpaRepository;
import ru.practicum.ewm.client.HitClient;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dictionary.EventSorting;
import ru.practicum.ewm.dictionary.EventStates;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventJpaRepository;
import ru.practicum.ewm.exceptions.*;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestsJpaRepository;
import ru.practicum.ewm.statistics.dto.EndpointHit;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserJpaRepository;
import utils.MyPageable;
import utils.QPredicates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.category.CategoryMapper.fromCategoryDto;
import static ru.practicum.ewm.dictionary.EventSorting.EVENT_DATE;
import static ru.practicum.ewm.dictionary.EventSorting.VIEWS;
import static ru.practicum.ewm.dictionary.EventStates.*;
import static ru.practicum.ewm.dictionary.RequestStates.CONFIRMED;
import static ru.practicum.ewm.dictionary.RequestStates.REJECTED;
import static ru.practicum.ewm.event.EventMapper.fromNewEventDto;
import static ru.practicum.ewm.event.EventMapper.toEventFullDto;
import static ru.practicum.ewm.event.model.QEvent.event;
import static ru.practicum.ewm.request.RequestMapper.toParticipationRequestDto;
import static utils.Constants.DATE_TIME_FORMATTER;
import static utils.Constants.SORT_BY_ID;


@Service
@Slf4j
public class EventsServiceImpl implements EventsService {

    private final EventJpaRepository eventJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final RequestsJpaRepository requestsJpaRepository;
    private final HitClient hitClient;
    private final StatsClient statsClient;

    @Autowired
    public EventsServiceImpl(EventJpaRepository eventJpaRepository,
                             UserJpaRepository userJpaRepository,
                             CategoryJpaRepository categoryJpaRepository,
                             RequestsJpaRepository requestsJpaRepository,
                             HitClient hitClient,
                             StatsClient statsClient) {
        this.eventJpaRepository = eventJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
        this.requestsJpaRepository = requestsJpaRepository;
        this.hitClient = hitClient;
        this.statsClient = statsClient;
    }

    public List<EventFullDto> getAllEventsByFilter(List<Long> users,
                                                   List<String> states,
                                                   List<Long> categories,
                                                   String rangeStart,
                                                   String rangeEnd,
                                                   Integer from,
                                                   Integer size) throws IncorrectEventStateException {
        Pageable page = new MyPageable(from, size, SORT_BY_ID);
        List<EventStates> eventStates = new ArrayList<>();
        LocalDateTime rangeStartFilter = LocalDateTime.now().minusYears(5);
        LocalDateTime rangeEndFilter = LocalDateTime.now().plusYears(5);
        if (rangeStart != null) {
            rangeStartFilter = LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER);
        }
        if (rangeEnd != null) {
            rangeEndFilter = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER);
        }

        if (states != null) {
            try {
                eventStates = states.stream()
                        .map(EventStates::valueOf)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                throw new IncorrectEventStateException("Задан несуществующий статус события");
            }
        }

        Predicate predicate = QPredicates.builder()
                .add(users, event.initiator.id::in)
                .add(eventStates, event.eventState::in)
                .add(categories, event.category.id::in)
                .add(rangeStartFilter, event.eventDate::after)
                .add(rangeEndFilter, event.eventDate::before)
                .buildAnd();

        Page<Event> requestPage = eventJpaRepository.findAll(predicate, page);

        return requestPage.getContent()
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto editEventByAdmin(Long eventId, AdminUpdateEventRequest updatedEvent) throws EventNotFoundException {
        Event event = eventJpaRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("События с id " + eventId + " не существует", "Событие не найдено в таблице"));

        if (updatedEvent.getAnnotation() != null) {
            event.setAnnotation(updatedEvent.getAnnotation());
        } else if (updatedEvent.getCategory() != null) {
            event.setCategory(fromCategoryDto(updatedEvent.getCategory()));
        } else if (updatedEvent.getDescription() != null) {
            event.setDescription(updatedEvent.getDescription());
        } else if (updatedEvent.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updatedEvent.getEventDate(), DATE_TIME_FORMATTER));
        } else if (updatedEvent.getLocation() != null) {
            event.setLon(updatedEvent.getLocation().getLon());
            event.setLat(updatedEvent.getLocation().getLat());
        } else if (updatedEvent.getPaid() != null) {
            event.setPaid(updatedEvent.getPaid());
        } else if (updatedEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updatedEvent.getParticipantLimit());
        } else if (updatedEvent.getRequestModeration() != null) {
            event.setRequestModeration(updatedEvent.getRequestModeration());
        } else if (updatedEvent.getTitle() != null) {
            event.setTitle(updatedEvent.getTitle());
        }

        eventJpaRepository.save(event);
        return toEventFullDto(event);
    }

    public EventFullDto publishEvent(Long eventId) throws EventNotFoundException, IncorrectEventParamsException {
        Event event = eventJpaRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("События с id " + eventId + " не существует", "Событие не найдено в таблице"));
        if (event.getEventDate().isAfter(LocalDateTime.now().minusHours(1)) && event.getEventState().equals(PENDING)) {
            event.setEventState(PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else {
            throw new IncorrectEventParamsException("Невозможно опубликовать событие", "До начала события мене часа, либо событие уже было опубликовано ранее");
        }
        return toEventFullDto(event);
    }

    public EventFullDto rejectEvent(Long eventId) throws EventNotFoundException, IncorrectEventParamsException {
        Event event = eventJpaRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("События с id " + eventId + " не существует", "Событие не найдено в таблице"));
        if (event.getEventState().equals(PUBLISHED)) {
            event.setEventState(CANCELED);
        } else {
            throw new IncorrectEventParamsException("Невозможно отклонить публикацию события", "Событие уже было опубликовано");
        }
        return toEventFullDto(event);
    }

    public List<EventShortDto> getAllPublicEventsByFilter(String text,
                                                          List<Long> categories,
                                                          Boolean paid,
                                                          String rangeStart,
                                                          String rangeEnd,
                                                          Boolean onlyAvailable,
                                                          String sort,
                                                          Integer from,
                                                          Integer size,
                                                          String ip,
                                                          String uri) throws IncorrectEventParamsException {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        }
        Sort eventSort;
        if (EventSorting.valueOf(sort).equals(EVENT_DATE)) {
            eventSort = Sort.by(Sort.Direction.DESC, "event_date");
        } else if (EventSorting.valueOf(sort).equals(VIEWS)) {
            eventSort = Sort.by(Sort.Direction.DESC, "views");
        } else {
            throw new IncorrectEventParamsException("Невозможно выполнить сортировку", "Некорректно заданы параметры сортировки");
        }
        Pageable page = new MyPageable(from, size, eventSort);
        LocalDateTime rangeStartFilter = LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER);
        LocalDateTime rangeEndFilter = LocalDateTime.now().plusYears(5);
        if (rangeEnd != null) {
            rangeEndFilter = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER);
        }

        Predicate predicate = QPredicates.builder()
                .add(PUBLISHED, event.eventState::eq)
                .add(text, event.annotation::containsIgnoreCase)
//                .add(text, event.description::containsIgnoreCase)
//                .add(categories, event.category.id::in)
//                .add(paid, event.paid::eq)
//                .add(rangeStartFilter, event.eventDate::after)
//                .add(rangeEndFilter, event.eventDate::before)
//                .add(onlyAvailable, (event.participantLimit.gt(event.confirmedRequests))::eq)
                .buildAnd();
        Page<Event> requestPage = eventJpaRepository.findAll(predicate, page);
        writeStats(ip, uri);
        return requestPage.getContent()
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto getEvent(Long eventId, String ip, String uri) throws EventNotFoundException {
        Event event = eventJpaRepository.findEventByIdAndEventState(eventId, PUBLISHED).orElseThrow(() -> new EventNotFoundException("События с id " + eventId + " не существует", "Не найдено опубликованное событие с заданным id"));
        writeStats(ip, uri);
        return toEventFullDto(event);
    }

    public List<EventShortDto> getAllEventsByUserId(Long userId, Integer from, Integer size) throws UserNotFoundException {
        User user = userJpaRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователя не существует", "Пользователь с id " + userId + " не найден"));
        Pageable page = new MyPageable(from, size, SORT_BY_ID);
        List<Event> events = eventJpaRepository.findByInitiator(user, page).getContent();
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto updateEventByUser(Long userId, UpdateEventRequest updatedEvent) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        checkUser(userId);
        checkEvent(updatedEvent.getEventId());
        Event event = eventJpaRepository.findById(updatedEvent.getEventId()).get();
        checkInitiator(userId, updatedEvent.getEventId());

        if (event.getEventState().equals(CANCELED) || event.getRequestModeration()) {
            if (LocalDateTime.parse(updatedEvent.getEventDate(), DATE_TIME_FORMATTER).isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ForbiddenException("Невозможно редактировать событие", "Начало события запланировано менее чем через 2 часа");
            }

            if (updatedEvent.getAnnotation() != null) {
                event.setAnnotation(updatedEvent.getAnnotation());
            } else if (updatedEvent.getCategory() != null) {
                event.setCategory(fromCategoryDto(updatedEvent.getCategory()));
            } else if (updatedEvent.getDescription() != null) {
                event.setDescription(updatedEvent.getDescription());
            } else if (updatedEvent.getEventDate() != null) {
                event.setEventDate(LocalDateTime.parse(updatedEvent.getEventDate(), DATE_TIME_FORMATTER));
            } else if (updatedEvent.getPaid() != null) {
                event.setPaid(updatedEvent.getPaid());
            } else if (updatedEvent.getParticipantLimit() != null) {
                event.setParticipantLimit(updatedEvent.getParticipantLimit());
            } else if (event.getEventState().equals(CANCELED)) {
                event.setRequestModeration(true);
            } else if (updatedEvent.getTitle() != null) {
                event.setTitle(updatedEvent.getTitle());
            }
            event.setEventState(PENDING);
        } else {
            throw new ForbiddenException("Невозможно редактировать событие", "Не выполнены условия для редактирования");
        }
        return toEventFullDto(eventJpaRepository.save(event));
    }

    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) throws UserNotFoundException, CategoryNotFoundException {
        User user = userJpaRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователя не существует", "Пользователь с id " + userId + " не найден"));
        Category category = categoryJpaRepository.findById(newEventDto.getCategory()).orElseThrow(() -> new CategoryNotFoundException("Невозможно создание события", "Указанная категория не существует"));
        Event event = fromNewEventDto(newEventDto, category, user);
        event = eventJpaRepository.save(event);
        return toEventFullDto(event);
    }

    public EventFullDto getEventByUser(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        checkUser(userId);
        checkEvent(eventId);
        Event event = eventJpaRepository.findById(eventId).get();
        checkInitiator(userId, eventId);
        return toEventFullDto(event);
    }

    public EventFullDto rejectEventByUser(Long userId, Long eventId) throws ForbiddenException, UserNotFoundException, EventNotFoundException {
        checkUser(userId);
        checkEvent(eventId);
        Event event = eventJpaRepository.findById(eventId).get();
        checkInitiator(userId, eventId);
        if (event.getEventState().equals(PENDING)) {
            event.setEventState(CANCELED);
            return toEventFullDto(event);
        } else {
            throw new ForbiddenException("Невозможно отменить событие", "Событие не находится в ожидании модерации");
        }
    }

    public List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        checkUser(userId);
        checkEvent(eventId);
        checkInitiator(userId, eventId);
        List<Request> requests = requestsJpaRepository.findByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto confirmEventRequest(Long userId, Long eventId, Long reqId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        checkUser(userId);
        checkEvent(eventId);
        checkInitiator(userId, eventId);
        Request request = requestsJpaRepository.findById(reqId).orElseThrow(() -> new EventNotFoundException("Запрос не найден", "Запроса с id " + reqId + " не существует"));
        if (!request.getEvent().getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Запрещено редактировать запрос", "Событие запроса не принадлежит текущему пользователю");
        }
        request.setStatus(CONFIRMED);
        return toParticipationRequestDto(request);
    }

    public ParticipationRequestDto rejectEventRequest(Long userId, Long eventId, Long reqId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        checkUser(userId);
        checkEvent(eventId);
        checkInitiator(userId, eventId);
        Request request = requestsJpaRepository.findById(reqId).orElseThrow(() -> new EventNotFoundException("Запрос не найден", "Запроса с id " + reqId + " не существует"));
        if (!request.getEvent().getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Запрещено редактировать запрос", "Событие запроса не принадлежит текущему пользователю");
        }
        request.setStatus(REJECTED);
        return toParticipationRequestDto(request);
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

    private void writeStats(String ip, String uri) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp("ewm-service");
        endpointHit.setIp(ip);
        endpointHit.setUri(uri);
        endpointHit.setTimestamp(LocalDateTime.now());
        hitClient.addStatInfo(endpointHit);
    }
}
