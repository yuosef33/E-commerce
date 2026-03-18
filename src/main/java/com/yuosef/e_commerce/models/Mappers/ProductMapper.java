package com.yuosef.e_commerce.models.Mappers;

import com.yuosef.e_commerce.models.Category;
import com.yuosef.e_commerce.models.Dtos.ProductRequest;
import com.yuosef.e_commerce.models.Dtos.ProductResponse;
import com.yuosef.e_commerce.models.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getCreatedAt()
        );
    }

    public Product toEntity(ProductRequest request, Category category) {
        Product p = new Product();
        p.setName(request.name());
        p.setDescription(request.description());
        p.setPrice(request.price());
        p.setStock(request.stock());
        p.setImageUrl(request.imageUrl());
        p.setCategory(category);
        return p;
    }
}