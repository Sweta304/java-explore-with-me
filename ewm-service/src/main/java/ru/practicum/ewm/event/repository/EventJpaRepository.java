package ru.practicum.ewm.event.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.dictionary.EventStates;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventJpaRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Page<Event> findAll(Predicate predicate, Pageable pageable);

    List<Event> findAllByCategoryId(Long categoryId);

    Optional<Event> findEventByIdAndEventState(Long id, EventStates state);

    Page<Event> findByInitiator(User user, Pageable pageable);

    List<Event> findByInitiatorId(Long userId);

    boolean existsByCategoryId(Long categoryId);

    List<Event> findAllByIdIn(List<Long> ids);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);
}
