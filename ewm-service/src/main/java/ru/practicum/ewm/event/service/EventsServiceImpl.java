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
import ru.practicum.ewm.event.repository.QPredicates;
import ru.practicum.ewm.exceptions.*;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.rating.repository.RatingJpaRepository;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestsJpaRepository;
import ru.practicum.ewm.statistics.dto.EndpointHit;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserJpaRepository;
import ru.practicum.ewm.utils.MyPageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.dictionary.EventSorting.*;
import static ru.practicum.ewm.dictionary.EventStates.*;
import static ru.practicum.ewm.dictionary.RequestStates.CONFIRMED;
import static ru.practicum.ewm.dictionary.RequestStates.REJECTED;
import static ru.practicum.ewm.event.EventMapper.fromNewEventDto;
import static ru.practicum.ewm.event.EventMapper.toEventFullDto;
import static ru.practicum.ewm.event.model.QEvent.event;
import static ru.practicum.ewm.request.RequestMapper.toParticipationRequestDto;
import static ru.practicum.ewm.utils.Constants.DATE_TIME_FORMATTER;
import static ru.practicum.ewm.utils.Constants.SORT_BY_ID;


@Service
@Slf4j
public class EventsServiceImpl implements EventsService {

    private final EventJpaRepository eventJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final RequestsJpaRepository requestsJpaRepository;
    private final RatingJpaRepository ratingJpaRepository;
    private final HitClient hitClient;
    private final StatsClient statsClient;

    @Autowired
    public EventsServiceImpl(EventJpaRepository eventJpaRepository,
                             UserJpaRepository userJpaRepository,
                             CategoryJpaRepository categoryJpaRepository,
                             RequestsJpaRepository requestsJpaRepository,
                             RatingJpaRepository ratingJpaRepository,
                             HitClient hitClient,
                             StatsClient statsClient) {
        this.eventJpaRepository = eventJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
        this.requestsJpaRepository = requestsJpaRepository;
        this.ratingJpaRepository = ratingJpaRepository;
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

        QPredicates qPredicates = QPredicates.builder();

        Predicate predicate = qPredicates
                .add(users, event.initiator.id::in)
                .add(categories, event.category.id::in)
                .add(rangeStartFilter, event.eventDate::after)
                .add(rangeEndFilter, event.eventDate::before)
                .buildAnd();

        if (!eventStates.isEmpty()) {
            predicate = qPredicates
                    .add(eventStates, event.eventState::in)
                    .buildAnd();
        }

        Page<Event> requestPage = eventJpaRepository.findAll(predicate, page);

        return requestPage.getContent()
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto editEventByAdmin(Long eventId, AdminUpdateEventRequest updatedEvent) throws EventNotFoundException, CategoryNotFoundException {
        Event event = eventJpaRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("События с id " + eventId + " не существует", "Событие не найдено в таблице"));
        Category category = categoryJpaRepository.findById(updatedEvent.getCategory()).orElseThrow(() -> new CategoryNotFoundException("Невозможно корректировать событие", "Указанная категория не существует"));


        if (updatedEvent.getAnnotation() != null) {
            event.setAnnotation(updatedEvent.getAnnotation());
        }
        if (updatedEvent.getCategory() != null) {
            event.setCategory(category);
        }
        if (updatedEvent.getDescription() != null) {
            event.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updatedEvent.getEventDate(), DATE_TIME_FORMATTER));
        }
        if (updatedEvent.getLocation() != null) {
            event.setLon(updatedEvent.getLocation().getLon());
            event.setLat(updatedEvent.getLocation().getLat());
        }
        if (updatedEvent.getPaid() != null) {
            event.setPaid(updatedEvent.getPaid());
        }
        if (updatedEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updatedEvent.getParticipantLimit());
        }
        if (updatedEvent.getRequestModeration() != null) {
            event.setRequestModeration(updatedEvent.getRequestModeration());
        }
        if (updatedEvent.getTitle() != null) {
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
        return toEventFullDto(eventJpaRepository.save(event));
    }

    public EventFullDto rejectEvent(Long eventId) throws EventNotFoundException, IncorrectEventParamsException {
        Event event = eventJpaRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("События с id " + eventId + " не существует", "Событие не найдено в таблице"));
        if (!event.getEventState().equals(PUBLISHED)) {
            event.setEventState(CANCELED);
        } else {
            throw new IncorrectEventParamsException("Невозможно отклонить публикацию события", "Событие уже было опубликовано");
        }
        return toEventFullDto(eventJpaRepository.save(event));
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
            eventSort = Sort.by(Sort.Direction.DESC, "eventDate");
        } else if (EventSorting.valueOf(sort).equals(VIEWS)) {
            eventSort = Sort.by(Sort.Direction.DESC, "views");
        } else if (EventSorting.valueOf(sort).equals(RATING)) {
            eventSort = Sort.by(Sort.Direction.DESC, "rating");
        } else {
            throw new IncorrectEventParamsException("Невозможно выполнить сортировку", "Некорректно заданы параметры сортировки");
        }
        Pageable page = new MyPageable(from, size, eventSort);
        LocalDateTime rangeStartFilter = LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER);
        LocalDateTime rangeEndFilter = LocalDateTime.now().plusYears(5);
        if (rangeEnd != null) {
            rangeEndFilter = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER);
        }

        QPredicates qPredicates = QPredicates.builder();

        qPredicates
                .add(PUBLISHED, event.eventState::eq)
                .add(categories, event.category.id::in)
                .add(text, event.annotation::containsIgnoreCase)
                .add(paid, event.paid::eq)
                .add(rangeStartFilter, event.eventDate::after)
                .add(rangeEndFilter, event.eventDate::before)
                .buildAnd();

        if (onlyAvailable != null && onlyAvailable) {
            qPredicates
                    .add(event.confirmedRequests, event.participantLimit::gt)
                    .buildAnd();
        }

        Predicate predicate = qPredicates.buildAnd();

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
        int views = getStats(List.of(uri), false);
        event.setViews(views);
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

    public EventFullDto updateEventByUser(Long userId, UpdateEventRequest updatedEvent) throws UserNotFoundException, EventNotFoundException, ForbiddenException, CategoryNotFoundException {
        checkUser(userId);
        checkEvent(updatedEvent.getEventId());
        Event event = eventJpaRepository.findById(updatedEvent.getEventId()).get();
        checkInitiator(userId, updatedEvent.getEventId());
        Category category = categoryJpaRepository.findById(updatedEvent.getCategory()).orElseThrow(() -> new CategoryNotFoundException("Невозможно изменение события", "Указанная категория не существует"));


        if (event.getEventState().equals(CANCELED) || event.getEventState().equals(PENDING)) {
            if (LocalDateTime.parse(updatedEvent.getEventDate(), DATE_TIME_FORMATTER).isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ForbiddenException("Невозможно редактировать событие", "Начало события запланировано менее чем через 2 часа");
            }

            if (updatedEvent.getAnnotation() != null) {
                event.setAnnotation(updatedEvent.getAnnotation());
            }
            if (updatedEvent.getCategory() != null) {
                event.setCategory(category);
            }
            if (updatedEvent.getDescription() != null) {
                event.setDescription(updatedEvent.getDescription());
            }
            if (updatedEvent.getEventDate() != null) {
                event.setEventDate(LocalDateTime.parse(updatedEvent.getEventDate(), DATE_TIME_FORMATTER));
            }
            if (updatedEvent.getPaid() != null) {
                event.setPaid(updatedEvent.getPaid());
            }
            if (updatedEvent.getParticipantLimit() != null) {
                event.setParticipantLimit(updatedEvent.getParticipantLimit());
            }
            if (event.getEventState().equals(CANCELED)) {
                event.setEventState(PENDING);
            }
            if (updatedEvent.getTitle() != null) {
                event.setTitle(updatedEvent.getTitle());
            }
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
        createNewRatingRecord(request.getRequester().getId(), eventId);
        return toParticipationRequestDto(requestsJpaRepository.save(request));
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
        removeRatingRecord(userId, eventId);
        updateEventRating(eventId);
        return toParticipationRequestDto(requestsJpaRepository.save(request));
    }

    public EventFullDto addLike(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        Event event = eventJpaRepository.findEventByIdAndEventState(eventId, PUBLISHED).orElseThrow(() -> new EventNotFoundException("События с id " + eventId + " не существует", "Не найдено опубликованное событие с заданным id"));
        User user = userJpaRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователя не существует", "Пользователь с id " + userId + " не найден"));
        if (event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ForbiddenException("Нельзя поставить лайк", "Вы еще не посетили событие");
        }
        Rating rating = ratingJpaRepository.findByVisitorAndEvent(user, event);
        if (rating != null) {
            rating.setLiked(true);
            if (rating.getDisliked()) {
                rating.setDisliked(false);
            }
        } else {
            throw new ForbiddenException("Нельзя поставить лайк", "Вы не являетесь участником события");
        }
        ratingJpaRepository.save(rating);
        updateEventRating(eventId);
        updateInitiatorRating(userId);
        return toEventFullDto(eventJpaRepository.save(event));
    }

    public EventFullDto addDislike(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, ForbiddenException {
        Event event = eventJpaRepository.findEventByIdAndEventState(eventId, PUBLISHED).orElseThrow(() -> new EventNotFoundException("События с id " + eventId + " не существует", "Не найдено опубликованное событие с заданным id"));
        User user = userJpaRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователя не существует", "Пользователь с id " + userId + " не найден"));
        if (event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ForbiddenException("Нельзя поставить дизлайк", "Вы еще не посетили событие");
        }
        Rating rating = ratingJpaRepository.findByVisitorAndEvent(user, event);
        if (rating != null) {
            rating.setDisliked(true);
            if (rating.getLiked()) {
                rating.setLiked(false);
            }
        } else {
            throw new ForbiddenException("Нельзя поставить дизлайк", "Вы не являетесь участником события");
        }
        ratingJpaRepository.save(rating);
        updateEventRating(eventId);
        updateInitiatorRating(userId);
        return toEventFullDto(eventJpaRepository.save(event));
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

    private int getStats(List<String> uris, Boolean unique) {
        return statsClient.getViews(LocalDateTime.now().minusYears(5).format(DATE_TIME_FORMATTER), LocalDateTime.now().plusYears(5).format(DATE_TIME_FORMATTER), uris, unique);
    }

    private void createNewRatingRecord(Long userId, Long eventId) {
        Event event = eventJpaRepository.findById(eventId).get();
        User user = userJpaRepository.findById(userId).get();
        Rating rating = new Rating();
        rating.setEvent(event);
        rating.setVisitor(user);
        rating.setLiked(false);
        rating.setDisliked(false);
        ratingJpaRepository.save(rating);
    }

    private void removeRatingRecord(Long userId, Long eventId) {
        Event event = eventJpaRepository.findById(eventId).get();
        User user = userJpaRepository.findById(userId).get();
        Rating rating = ratingJpaRepository.findByVisitorAndEvent(user, event);
        if (rating != null) {
            ratingJpaRepository.delete(rating);
        }
    }

    private void updateEventRating(Long eventId) {
        Event event = eventJpaRepository.findById(eventId).get();
        List<Rating> ratings = ratingJpaRepository.findByEvent(event);
        Long likes = ratings.stream()
                .filter(Rating::getLiked)
                .count();
        Long dislikes = ratings.stream()
                .filter(Rating::getDisliked)
                .count();
        Long rating = likes - dislikes;
        event.setRating(rating);
        event.setLikes(likes);
        event.setDislikes(dislikes);
    }

    private void updateInitiatorRating(Long userId) {
        User user = userJpaRepository.findById(userId).get();
        List<Event> userEvents = eventJpaRepository.findByInitiatorId(user.getId());
        Double rating = 0.0;
        for (Event x : userEvents) {
            rating = rating + x.getRating();
        }
        rating = rating / userEvents.size();
        user.setUserRating(rating);
        userJpaRepository.save(user);
    }
}
