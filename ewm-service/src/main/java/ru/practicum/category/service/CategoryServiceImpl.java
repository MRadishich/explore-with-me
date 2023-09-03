package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryInputDto;
import ru.practicum.category.dto.CategoryOutputDto;
import ru.practicum.category.entity.Category;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.model.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

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
    public void deleteCategory(int catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("category with id =" + catId + "was not found.");
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryOutputDto updateCategory(int catId, CategoryInputDto categoryInputDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Category with id =" + catId + " was not found.")
        );

        category.setName(categoryInputDto.getName());
        category = categoryRepository.save(category);

        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CategoryOutputDto> getCategories(int from, int size) {
        Page<Category> categories = categoryRepository.findAll(PageRequest.of(from, size));

        return categories.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryOutputDto getCategory(int catId) {
        return categoryRepository.findById(catId)
                .map(CategoryMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Category with id =" + catId + " was not found."));
    }
}
