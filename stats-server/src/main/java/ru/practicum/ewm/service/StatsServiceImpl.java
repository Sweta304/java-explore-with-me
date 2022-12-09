package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.repository.StatsJpaRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.EndpointMapper.fromEndPointHitDto;
import static ru.practicum.ewm.utils.Constants.DATE_TIME_FORMATTER;


@Service
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsJpaRepository statsJpaRepository;

    @Autowired
    public StatsServiceImpl(StatsJpaRepository statsJpaRepository) {
        this.statsJpaRepository = statsJpaRepository;
    }

    public void addStatInfo(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = fromEndPointHitDto(endpointHitDto);
        statsJpaRepository.save(endpointHit);
    }

    public List<ViewStats> getStatistic(String start, String end, List<String> uris, Boolean unique) {
        start = URLDecoder.decode(start, StandardCharsets.UTF_8);
        end = URLDecoder.decode(end, StandardCharsets.UTF_8);
        List<ViewStats> endpointHits;
        LocalDateTime startDate = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
        if (unique) {
            endpointHits = statsJpaRepository.findByTimestampDistinct(startDate, endDate, uris);
        } else {
            endpointHits = statsJpaRepository.findByTimestamp(startDate, endDate, uris);
        }
        return endpointHits;
    }

}
