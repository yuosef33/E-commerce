package com.yuosef.e_commerce.controller;

import com.yuosef.e_commerce.models.Dtos.ApiResponse;
import com.yuosef.e_commerce.models.Dtos.CategoryRequest;
import com.yuosef.e_commerce.models.Dtos.CategoryResponse;
import com.yuosef.e_commerce.services.Impl.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping//get all categories
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Categories fetched successfully", categoryService.getAll()));
    }

    @GetMapping("/{id}")//get category by id
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Category fetched successfully", categoryService.getById(id)));
    }

    @PostMapping// create category just for admins
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Category created successfully", categoryService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Category updated successfully", categoryService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Category deleted successfully"));
    }
}