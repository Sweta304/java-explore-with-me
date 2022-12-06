package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
public class NewCompilationDto {
    private List<Long> events;
    boolean pinned;
    @NotNull
    String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewCompilationDto that = (NewCompilationDto) o;
        return pinned == that.pinned && Objects.equals(events, that.events) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(events, pinned, title);
    }
}
