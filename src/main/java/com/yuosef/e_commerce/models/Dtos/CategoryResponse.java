package com.yuosef.e_commerce.models.Dtos;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        String slug
) {}