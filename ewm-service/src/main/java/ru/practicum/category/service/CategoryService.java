package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryInputDto;
import ru.practicum.category.dto.CategoryOutputDto;

import java.util.List;

public interface CategoryService {
    CategoryOutputDto createCategory(CategoryInputDto categoryInputDto);

    void deleteCategory(int catId);

    CategoryOutputDto updateCategory(int categoryId, CategoryInputDto categoryInputDto);

    List<CategoryOutputDto> getCategories(int from, int size);

    CategoryOutputDto getCategory(int catId);
}
