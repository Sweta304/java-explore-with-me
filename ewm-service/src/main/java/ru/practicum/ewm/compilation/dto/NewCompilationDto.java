package ru.practicum.ewm.compilation.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Jacksonized
@EqualsAndHashCode
public class NewCompilationDto {
    private List<Long> events;
    boolean pinned;
    @NotNull
    String title;

}
