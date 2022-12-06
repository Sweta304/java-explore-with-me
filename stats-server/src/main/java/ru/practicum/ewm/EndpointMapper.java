package ru.practicum.ewm;


import ru.practicum.ewm.dto.EndpointHitDto;
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

    public static EndpointHit fromEndPointHitDto(EndpointHitDto endPointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(endPointHitDto.getApp());
        endpointHit.setIp(endPointHitDto.getIp());
        endpointHit.setUri(endPointHitDto.getUri());
        endpointHit.setTimestamp(endPointHitDto.getTimestamp());
        return endpointHit;
    }
}
