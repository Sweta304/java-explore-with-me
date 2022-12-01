package ru.practicum.ewm.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.category.model.Category;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, Long>, QuerydslPredicateExecutor<Category> {

    Page<Category> findAll(Pageable page);
}
