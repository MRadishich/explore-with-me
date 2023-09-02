package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryInputDto;
import ru.practicum.category.dto.CategoryOutputDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryOutputDto createCategory(@RequestBody @Valid CategoryInputDto categoryInputDto) {
        return categoryService.createCategory(categoryInputDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryOutputDto updateCategory(@PathVariable int catId, @RequestBody @Valid CategoryInputDto categoryInputDto) {
        return categoryService.updateCategory(catId, categoryInputDto);
    }
}
