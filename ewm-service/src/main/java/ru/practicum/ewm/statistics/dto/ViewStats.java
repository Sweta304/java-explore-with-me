package ru.practicum.ewm.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
