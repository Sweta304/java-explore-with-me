package ru.practicum.ewm.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@Repository
public interface CompilationJpaRepository extends JpaRepository<Compilation, Long>, QuerydslPredicateExecutor<Compilation> {
    List<Compilation> findAllByEventsContaining(Event event);

    Page<Compilation> findAll(Pageable pageable);
    Page<Compilation> findByPinned(Boolean pinned, Pageable pageable);
}
