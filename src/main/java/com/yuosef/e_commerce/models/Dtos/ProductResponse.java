package com.yuosef.e_commerce.models.Dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String imageUrl,
        String categoryName,
        LocalDateTime createdAt
) {}