package ru.practicum.compilation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.compilation.dto.CompilationCreateDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.request.repository.RequestRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final RequestRepository requestRepository;

    public CompilationDto toDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                compilation.getEvents() == null ? List.of() :
                        compilation.getEvents().stream()
                                .map(event -> EventMapper.toShortDto(event, requestRepository.countConfirmedRequests(event.getId())))
                                .collect(Collectors.toList())
        );
    }

    public Compilation toCompilation(CompilationCreateDto compilationCreateDto) {
        return new Compilation(
                null,
                compilationCreateDto.getTitle(),
                compilationCreateDto.getPinned(),
                null
        );
    }
}
