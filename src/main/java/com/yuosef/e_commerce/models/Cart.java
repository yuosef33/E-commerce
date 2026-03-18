package com.yuosef.e_commerce.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private Long userId;
    private Map<Long, CartItem> items = new LinkedHashMap<>(); // productId -> CartItem

    public Cart(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotal() {
        return items.values().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}