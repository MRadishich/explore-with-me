package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationCreateDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.entity.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto createCompilation(CompilationCreateDto compilationCreateDto) {
        Compilation compilation = compilationMapper.toCompilation(compilationCreateDto);

        if (compilationCreateDto.getEvents() != null && !compilationCreateDto.getEvents().isEmpty()) {
            List<Event> eventsFromDto = eventRepository.findAllById(compilationCreateDto.getEvents());
            compilation.setEvents(eventsFromDto);
        }

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto updateCompilationById(UpdateCompilationRequest compilationRequest, int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found."));

        if (compilationRequest.getTitle() != null && !compilationRequest.getTitle().isBlank()) {
            compilation.setTitle(compilationRequest.getTitle());
        }

        if (compilationRequest.getPinned() != null) {
            compilation.setPinned(compilationRequest.getPinned());
        }

        if (compilationRequest.getEvents() != null && !compilationRequest.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(compilationRequest.getEvents());
            compilation.setEvents(events);
        }

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilationById(int compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found."));

        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        Pageable page = PageRequest.of(from == 0 ? 0 : from / size, size);

        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, page)
                    .stream()
                    .map(compilationMapper::toDto)
                    .collect(Collectors.toList());
        }
        return compilationRepository.findAll(page)
                .stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(int compId) {
        return compilationMapper.toDto(compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found.")));
    }
}
