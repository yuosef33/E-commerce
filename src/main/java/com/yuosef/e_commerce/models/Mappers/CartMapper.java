package com.yuosef.e_commerce.models.Mappers;

import com.yuosef.e_commerce.models.Cart;
import com.yuosef.e_commerce.models.Dtos.CartItemResponse;
import com.yuosef.e_commerce.models.Dtos.CartResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().values().stream()
                .map(item -> new CartItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        return new CartResponse(
                cart.getUserId(),
                items,
                cart.getTotal(),
                items.size()
        );
    }
}