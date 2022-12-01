package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    boolean pinned;
    String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompilationDto that = (CompilationDto) o;
        return pinned == that.pinned && Objects.equals(id, that.id) && Objects.equals(events, that.events) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, events, pinned, title);
    }
}
