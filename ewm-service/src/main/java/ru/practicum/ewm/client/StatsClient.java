package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.statistics.dto.ViewStats;

import java.util.List;

@Service
public class StatsClient {
    private static final String STATS_PREFIX = "/stats";
    private String serverUrl;

    public StatsClient(@Value("${stats-server.url}") String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public int getViews(String start, String end, List<String> uris, Boolean unique) {
        String url = serverUrl.concat(STATS_PREFIX).concat("?start={start}&end={end}&uris={uris}&unique={unique}");
        url = url.replace("{start}", start);
        url = url.replace("{end}", end);
        url = url.replace("{uris}", uris.toString().replace("[", "").replace("]", "").replace(" ", ""));
        url = url.replace("{unique}", unique.toString());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ViewStats[]> responseEntity =
                restTemplate.getForEntity(url, ViewStats[].class);
        ViewStats[] objects = responseEntity.getBody();
        return objects.length;
    }
}