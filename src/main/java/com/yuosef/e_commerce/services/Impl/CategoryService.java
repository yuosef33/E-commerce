package com.yuosef.e_commerce.services.Impl;

import com.yuosef.e_commerce.models.Category;
import com.yuosef.e_commerce.models.Dtos.CategoryRequest;
import com.yuosef.e_commerce.models.Dtos.CategoryResponse;
import com.yuosef.e_commerce.models.Mappers.CategoryMapper;
import com.yuosef.e_commerce.models.exceptions.ResourceNotFoundException;
import com.yuosef.e_commerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Category with this name already exists");
        }
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse getById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getName().equals(request.name()) &&
                categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Category with this name already exists");
        }

        category.setName(request.name());
        category.setDescription(request.description());
        category.setSlug(generateSlug(request.name()));
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category with existing products");
        }

        categoryRepository.delete(category);
    }

    private String generateSlug(String name) {
        return name.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-");
    }
}