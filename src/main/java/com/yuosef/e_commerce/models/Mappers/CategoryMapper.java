package com.yuosef.e_commerce.models.Mappers;

import com.yuosef.e_commerce.models.Category;
import com.yuosef.e_commerce.models.Dtos.CategoryRequest;
import com.yuosef.e_commerce.models.Dtos.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getSlug()
        );
    }

    public Category toEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        category.setSlug(generateSlug(request.name()));
        return category;
    }

    private String generateSlug(String name) {
        return name.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-");
    }
}