package ru.practicum.ewm.statistics.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class EndpointHit {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
