package com.yuosef.e_commerce.models.Dtos;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        int quantity,
        BigDecimal priceAtPurchase,
        BigDecimal subtotal
) {}
