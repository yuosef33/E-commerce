package com.yuosef.e_commerce.controller;

import com.yuosef.e_commerce.models.Dtos.ApiResponse;
import com.yuosef.e_commerce.models.Dtos.ProductRequest;
import com.yuosef.e_commerce.models.Dtos.ProductResponse;
import com.yuosef.e_commerce.services.Impl.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // PUBLIC - any user can browse
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Products fetched successfully",
                productService.getAll(categoryId, search, pageable)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("get product by id",productService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("created successfully",productService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id,
                                                               @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("updated successfully",productService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("deleted successfully",null));
    }
}