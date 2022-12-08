package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.statistics.dto.EndpointHit;
import ru.practicum.ewm.statistics.dto.ViewStats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    private static final String HIT_PREFIX = "/hit";
    private static final String STATS_PREFIX = "/stats";

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addStatInfo(EndpointHit endpointHit) {
        return post(HIT_PREFIX, endpointHit);
    }

    public ViewStats[] getViewStat(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        parameters.put("uris", uris);
        parameters.put("unique", unique);
        ResponseEntity<ViewStats[]> responseEntity = getForEntity(STATS_PREFIX.concat("?start={start}&end={end}&uris={uris}&unique={unique}"), ViewStats[].class, parameters);
        return responseEntity.getBody();
    }
}
