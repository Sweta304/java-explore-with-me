package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsJpaRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.ewm.dto.ViewStats (eh.app, eh.uri, count(eh)) " +
            "from EndpointHit as eh " +
            "where eh.timestamp >= :start " +
            "and eh.timestamp <= :end " +
            "and eh.uri in (:uris) " +
            "group by eh.app, eh.uri, eh.ip")
    List<ViewStats> findByTimestampDistinct(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

    @Query("select new ru.practicum.ewm.dto.ViewStats (eh.app, eh.uri, count(eh)) " +
            "from EndpointHit as eh " +
            "where eh.timestamp >= :start " +
            "and eh.timestamp <= :end " +
            "and eh.uri in (:uris) " +
            "group by eh.app, eh.uri")
    List<ViewStats> findByTimestamp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);
}
