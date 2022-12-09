package ru.practicum.ewm.request;


import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;

import java.time.LocalDateTime;

import static ru.practicum.ewm.utils.Constants.DATE_TIME_FORMATTER;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto
                .builder()
                .created(request.getCreated().format(DATE_TIME_FORMATTER))
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }

    public static Request fromParticipationRequestDto(ParticipationRequestDto participationRequestDto) {
        Request request = new Request();
        request.setCreated(LocalDateTime.parse(participationRequestDto.getCreated(), DATE_TIME_FORMATTER));
        request.setStatus(participationRequestDto.getStatus());
        return request;
    }
}
