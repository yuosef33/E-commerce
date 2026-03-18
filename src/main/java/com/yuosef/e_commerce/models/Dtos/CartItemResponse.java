package com.yuosef.e_commerce.models.Dtos;

import java.math.BigDecimal;

public record CartItemResponse(
        Long productId,
        String productName,
        BigDecimal price,
        int quantity,
        BigDecimal subtotal
) {}