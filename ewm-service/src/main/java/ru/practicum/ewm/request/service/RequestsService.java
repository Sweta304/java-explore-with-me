package ru.practicum.ewm.request.service;


import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.exceptions.ForbiddenException;
import ru.practicum.ewm.exceptions.UserNotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestsService {

    List<ParticipationRequestDto> getAllUserRequests(Long userId) throws UserNotFoundException;

    ParticipationRequestDto addRequest(Long userId, Long eventId) throws ForbiddenException, UserNotFoundException, EventNotFoundException;

    ParticipationRequestDto cancelRequest(Long userId, Long requestId) throws UserNotFoundException, EventNotFoundException;
}
