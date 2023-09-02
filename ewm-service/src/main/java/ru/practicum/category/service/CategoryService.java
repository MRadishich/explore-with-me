package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryInputDto;
import ru.practicum.category.dto.CategoryOutputDto;

public interface CategoryService {
    CategoryOutputDto createCategory(CategoryInputDto categoryInputDto);

    void deleteCategory(int categoryId);

    CategoryOutputDto updateCategory(int categoryId, CategoryInputDto categoryInputDto);
}
