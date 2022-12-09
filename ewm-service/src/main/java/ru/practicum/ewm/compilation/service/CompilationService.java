package ru.practicum.ewm.compilation.service;


import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.exceptions.CompilationNotFoundException;
import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.exceptions.ForbiddenException;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId) throws CompilationNotFoundException;

    void deleteEventFromCompilation(Long compId, Long eventId) throws CompilationNotFoundException, EventNotFoundException;

    void addEventToCompilation(Long compId, Long eventId) throws CompilationNotFoundException,
            EventNotFoundException, ForbiddenException;

    void unpinCompilation(Long compId) throws CompilationNotFoundException;

    void pinCompilation(Long compId) throws CompilationNotFoundException;

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilation(Long compId) throws CompilationNotFoundException;
}
