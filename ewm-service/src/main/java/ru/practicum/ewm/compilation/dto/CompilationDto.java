package ru.practicum.ewm.compilation.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.event.dto.EventShortDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
@EqualsAndHashCode
public class CompilationDto {
    private List<EventShortDto> events;
    private Long id;
    @NotNull
    boolean pinned;
    @NotNull
    private String title;

}
