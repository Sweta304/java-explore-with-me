package ru.practicum.ewm.compilation;


import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ru.practicum.ewm.event.EventMapper.toEventShortDto;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        Set<Event> events = compilation.getEvents();
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        events.forEach(x -> eventShortDtos.add(toEventShortDto(x)));
        return CompilationDto
                .builder()
                .events(eventShortDtos)
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation fromCompilationDto(CompilationDto compilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(compilationDto.getTitle());
        compilation.setPinned(compilationDto.isPinned());
        return compilation;
    }

    public static Compilation fromNewCompilationDto(NewCompilationDto compilationDto, Set<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setEvents(events);
        compilation.setTitle(compilationDto.getTitle());
        compilation.setPinned(compilationDto.isPinned());
        return compilation;
    }
}
