package ru.practicum.ewm.service;


import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStats;

import java.util.List;

public interface StatsService {

    void addStatInfo(EndpointHitDto endpointHitDto);

    List<ViewStats> getStatistic(String start, String end, List<String> uris, Boolean unique);
}
