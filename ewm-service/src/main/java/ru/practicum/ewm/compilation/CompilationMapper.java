package ru.practicum.ewm.compilation;


import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        List<EventShortDto> eventShortDtos = new ArrayList(compilation.getEvents());
        return CompilationDto
                .builder()
                .id(compilation.getId())
                .events(eventShortDtos)
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

    public static Compilation fromNewCompilationDto(NewCompilationDto compilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(compilationDto.getTitle());
        compilation.setPinned(compilationDto.isPinned());
        return compilation;
    }
}
