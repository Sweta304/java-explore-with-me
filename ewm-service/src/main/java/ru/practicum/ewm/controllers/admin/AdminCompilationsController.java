package ru.practicum.ewm.controllers.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.exceptions.CompilationNotFoundException;
import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.exceptions.ForbiddenException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationsController {
    private final CompilationService compilationService;

    @Autowired
    public AdminCompilationsController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Админ создает новую подборку {}", newCompilationDto.getTitle());
        return compilationService.addCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable @Positive Long compId) throws CompilationNotFoundException {
        log.info("Админ удаляет подборку с id {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable @Positive Long compId,
                                           @PathVariable @Positive Long eventId) throws CompilationNotFoundException,
            EventNotFoundException, ForbiddenException {
        log.info("Админ удаляет событие {} из подборки {}", eventId, compId);
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable @Positive Long compId,
                                           @PathVariable @Positive Long eventId) throws CompilationNotFoundException,
            EventNotFoundException, ForbiddenException {
        log.info("Админ удаляет событие {} из подборки {}", eventId, compId);
        compilationService.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unpinCompilation(@PathVariable @Positive Long compId) throws CompilationNotFoundException {
        log.info("Админ открепляет подборку {} с главной страницы", compId);
        compilationService.unpinCompilation(compId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinCompilation(@PathVariable @Positive Long compId) throws CompilationNotFoundException {
        log.info("Админ открепляет подборку {} с главной страницы", compId);
        compilationService.pinCompilation(compId);
    }
}
