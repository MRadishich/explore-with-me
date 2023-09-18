package ru.practicum.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryInputDto;
import ru.practicum.category.dto.CategoryOutputDto;
import ru.practicum.category.entity.Category;

@UtilityClass
public class CategoryMapper {
    public static Category toCategory(CategoryInputDto categoryInputDto) {
        return new Category(
                null,
                categoryInputDto.getName()
        );
    }

    public static CategoryOutputDto toDto(Category category) {
        return new CategoryOutputDto(
                category.getId(),
                category.getName()
        );
    }
}
