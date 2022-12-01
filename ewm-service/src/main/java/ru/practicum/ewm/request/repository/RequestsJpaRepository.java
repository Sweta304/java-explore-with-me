package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestsJpaRepository extends JpaRepository<Request, Long>, QuerydslPredicateExecutor<Request> {
    List<Request> findByEventId(Long eventId);
    List<Request> findByRequesterId(Long requesterId);
    Optional<Request> findByEventIdAndRequesterId(Long eventId, Long requesterId);
}
