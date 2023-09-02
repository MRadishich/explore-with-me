package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryInputDto;
import ru.practicum.category.dto.CategoryOutputDto;
import ru.practicum.category.entity.Category;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.model.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryOutputDto createCategory(CategoryInputDto categoryInputDto) {
        Category category = CategoryMapper.toCategory(categoryInputDto);
        category = categoryRepository.save(category);

        return CategoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(int categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("category with id =" + categoryId + "was not found.");
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryOutputDto updateCategory(int categoryId, CategoryInputDto categoryInputDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Category with id =" + categoryId + " was not found.")
        );

        category.setName(categoryInputDto.getName());
        category = categoryRepository.save(category);

        return CategoryMapper.toDto(category);
    }
}
