package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.service.StatsService;

import java.util.List;

@Slf4j
@RestController
public class StatisticsController {
    private final StatsService statsService;

    @Autowired
    public StatisticsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public void addStatInfo(@RequestBody EndpointHitDto endpointHit) {
        log.info("Добавление новой записи в статистику запросов");
        statsService.addStatInfo(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStatistic(@RequestParam String start,
                                        @RequestParam String end,
                                        @RequestParam(required = false) List<String> uris,
                                        @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Получение статистики запросов с {} по {}", start, end);
        return statsService.getStatistic(start, end, uris, unique);
    }
}
