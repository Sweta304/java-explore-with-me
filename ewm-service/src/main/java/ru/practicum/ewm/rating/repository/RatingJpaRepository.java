package ru.practicum.ewm.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.user.model.User;

import java.util.List;

@Repository
public interface RatingJpaRepository extends JpaRepository<Rating, Long>, QuerydslPredicateExecutor<Rating> {
    Rating findByVisitorAndEvent(User visitor, Event event);

    List<Rating> findByEvent(Event event);
}
