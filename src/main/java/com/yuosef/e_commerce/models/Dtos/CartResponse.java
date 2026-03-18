package com.yuosef.e_commerce.models.Dtos;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long userId,
        List<CartItemResponse> items,
        BigDecimal total,
        int itemCount
) {}