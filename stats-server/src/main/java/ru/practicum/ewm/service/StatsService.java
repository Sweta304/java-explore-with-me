package ru.practicum.ewm.service;


import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.model.EndpointHit;

import java.util.List;

public interface StatsService {

    void addStatInfo(EndpointHit endpointHit);

    List<ViewStats> getStatistic(String start, String end, List<String> uris, Boolean unique);
}
