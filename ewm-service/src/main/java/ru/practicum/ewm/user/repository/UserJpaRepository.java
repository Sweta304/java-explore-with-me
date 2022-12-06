package ru.practicum.ewm.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.user.model.User;

import java.util.List;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query(value = "select * from users " +
            "where id in (:bookerId)",
            nativeQuery = true)
    List<User> findAllByList(List<Long> bookerId);

    @Query(value = "select * from users " +
            "where id in (:bookerId)",
            nativeQuery = true)
    Page<User> findAllByList(List<Long> bookerId, Pageable pageable);

    User findByName(String name);
}
