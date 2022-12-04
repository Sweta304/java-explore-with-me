package ru.practicum.ewm;


import ru.practicum.ewm.dto.ViewStats;
import ru.practicum.ewm.model.EndpointHit;

public class EndpointMapper {

    public static ViewStats toViewStats(EndpointHit endpointHit, Long hits) {
        return ViewStats
                .builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .hits(hits)
                .build();
    }
}
