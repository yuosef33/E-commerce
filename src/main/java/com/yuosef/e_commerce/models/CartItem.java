package com.yuosef.e_commerce.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    // constructor, getters, setters
}