package com.yuosef.e_commerce.models.Mappers;

import com.yuosef.e_commerce.models.Dtos.OrderItemResponse;
import com.yuosef.e_commerce.models.Dtos.OrderResponse;
import com.yuosef.e_commerce.models.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceAtPurchase(),
                        item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getCity(),
                order.getCountry(),
                items,
                order.getCreatedAt()
        );
    }
}