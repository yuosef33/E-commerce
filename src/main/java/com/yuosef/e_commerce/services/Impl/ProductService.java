package com.yuosef.e_commerce.services.Impl;

import com.yuosef.e_commerce.models.Category;
import com.yuosef.e_commerce.models.Dtos.ProductRequest;
import com.yuosef.e_commerce.models.Dtos.ProductResponse;
import com.yuosef.e_commerce.models.Mappers.ProductMapper;
import com.yuosef.e_commerce.models.Product;
import com.yuosef.e_commerce.models.exceptions.ResourceNotFoundException;
import com.yuosef.e_commerce.repository.CategoryRepository;
import com.yuosef.e_commerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Product product = productMapper.toEntity(request, category);
        return productMapper.toResponse(productRepository.save(product));
    }

    public Page<ProductResponse> getAll(Long categoryId, String search, Pageable pageable) {
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId, pageable)
                    .map(productMapper::toResponse);
        }
        if (search != null && !search.isBlank()) {
            return productRepository.findByNameContainingIgnoreCase(search, pageable)
                    .map(productMapper::toResponse);
        }
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    public ProductResponse getById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setImageUrl(request.imageUrl());
        product.setCategory(category);
        return productMapper.toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id))
            throw new ResourceNotFoundException("Product not found");
        productRepository.deleteById(id);
    }
}