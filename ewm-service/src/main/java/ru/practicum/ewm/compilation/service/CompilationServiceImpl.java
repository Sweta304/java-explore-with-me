package ru.practicum.ewm.compilation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationJpaRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventJpaRepository;
import ru.practicum.ewm.exceptions.CompilationNotFoundException;
import ru.practicum.ewm.exceptions.EventNotFoundException;
import ru.practicum.ewm.exceptions.ForbiddenException;
import utils.MyPageable;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.compilation.CompilationMapper.fromNewCompilationDto;
import static ru.practicum.ewm.compilation.CompilationMapper.toCompilationDto;
import static utils.Constants.sortById;


@Service
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationJpaRepository compilationJpaRepository;
    private final EventJpaRepository eventJpaRepository;

    @Autowired
    public CompilationServiceImpl(CompilationJpaRepository compilationJpaRepository, EventJpaRepository eventJpaRepository) {
        this.compilationJpaRepository = compilationJpaRepository;
        this.eventJpaRepository = eventJpaRepository;
    }

    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = fromNewCompilationDto(newCompilationDto);
        return toCompilationDto(compilationJpaRepository.save(compilation));
    }

    public void deleteCompilation(Long compId) throws CompilationNotFoundException {
        Compilation compilation = compilationJpaRepository.findById(compId).orElseThrow(() -> new CompilationNotFoundException("подборки с id " + compId + " не существует", "в списке подборок не существует запрошенной подборки"));
        compilationJpaRepository.delete(compilation);
        log.info("удалена подборка с id {}", compId);
    }

    public void deleteEventFromCompilation(Long compId, Long eventId) throws CompilationNotFoundException, EventNotFoundException, ForbiddenException {
        Compilation compilation = compilationJpaRepository.findById(compId).orElseThrow(() -> new CompilationNotFoundException("подборки с id " + compId + " не существует", "в списке подборок не существует запрошенной подборки"));
        Event event = eventJpaRepository.findById(compId).orElseThrow(() -> new EventNotFoundException("события с id " + compId + " не существует", "в списке событий не существует запрошенного события"));
        if (compilation.getEvents().contains(event)) {
            compilation.getEvents().remove(event);
            log.info("удалено событие {} из подборки {}", eventId, compId);
        } else {
            log.info("неудачная попытка удалить событие {} из подборки {}", eventId, compId);
            throw new ForbiddenException("Невозможно удалить событие", "Событие " + eventId + " не найдено в подборке " + compId);
        }
    }

    public void addEventToCompilation(Long compId, Long eventId) throws CompilationNotFoundException, EventNotFoundException, ForbiddenException {
        Compilation compilation = compilationJpaRepository.findById(compId).orElseThrow(() -> new CompilationNotFoundException("подборки с id " + compId + " не существует", "в списке подборок не существует запрошенной подборки"));
        Event event = eventJpaRepository.findById(compId).orElseThrow(() -> new EventNotFoundException("события с id " + compId + " не существует", "в списке событий не существует запрошенного события"));
        if (compilationJpaRepository.findAllByEventsContaining(event).isEmpty()) {
            compilation.getEvents().add(event);
            log.info("добавлено событие {} в подборку {}", eventId, compId);
        } else {
            log.info("неудачная попытка добавить событие {} в подборку {}", eventId, compId);
            throw new ForbiddenException("Невозможно добавить событие", "Событие " + eventId + " участвует у другой подборке");
        }
    }

    public void unpinCompilation(Long compId) throws CompilationNotFoundException {
        Compilation compilation = compilationJpaRepository.findById(compId).orElseThrow(() -> new CompilationNotFoundException("подборки с id " + compId + " не существует", "в списке подборок не существует запрошенной подборки"));
        compilation.setPinned(false);
    }

    public void pinCompilation(Long compId) throws CompilationNotFoundException {
        Compilation compilation = compilationJpaRepository.findById(compId).orElseThrow(() -> new CompilationNotFoundException("подборки с id " + compId + " не существует", "в списке подборок не существует запрошенной подборки"));
        compilation.setPinned(true);
    }

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable page = new MyPageable(from, size, sortById);
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationJpaRepository.findAll(page).getContent();
        } else {
            compilations = compilationJpaRepository.findByPinned(pinned, page).getContent();
        }
        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    public CompilationDto getCompilation(Long compId) throws CompilationNotFoundException {
        Compilation compilation = compilationJpaRepository.findById(compId).orElseThrow(() -> new CompilationNotFoundException("Подборка не найдена", "Подборки с id " + compId + "не существует"));
        return toCompilationDto(compilation);
    }

}
