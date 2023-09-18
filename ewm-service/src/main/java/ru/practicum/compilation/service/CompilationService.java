package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationCreateDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(CompilationCreateDto compilationDto);

    CompilationDto updateCompilationById(UpdateCompilationRequest updateCompilationRequest, int compId);

    void deleteCompilationById(int compId);

    List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(int compId);
}
