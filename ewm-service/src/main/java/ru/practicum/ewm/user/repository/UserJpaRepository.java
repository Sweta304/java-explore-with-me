package ru.practicum.ewm.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.user.model.User;

import java.util.List;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    User findByName(String name);
}
