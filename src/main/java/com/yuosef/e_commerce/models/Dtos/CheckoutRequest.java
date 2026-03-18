package com.yuosef.e_commerce.models.Dtos;

import jakarta.validation.constraints.NotBlank;

public record CheckoutRequest(
        @NotBlank String shippingAddress,
        @NotBlank String city,
        @NotBlank String country
) {}
