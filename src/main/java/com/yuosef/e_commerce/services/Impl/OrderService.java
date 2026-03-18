package com.yuosef.e_commerce.services.Impl;

import com.yuosef.e_commerce.models.*;
import com.yuosef.e_commerce.models.Dtos.CheckoutRequest;
import com.yuosef.e_commerce.models.Dtos.OrderResponse;
import com.yuosef.e_commerce.models.Mappers.OrderMapper;
import com.yuosef.e_commerce.models.exceptions.ResourceNotFoundException;
import com.yuosef.e_commerce.repository.OrderRepository;
import com.yuosef.e_commerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;

    public OrderResponse checkout(User user, CheckoutRequest request) {
        Cart cart = cartService.getCart(user.getId());

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // validate stock for all items first
        for (CartItem cartItem : cart.getItems().values()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException(
                        "Not enough stock for product: " + product.getName()
                );
            }
        }

        // build order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.COMPLETED);
        order.setShippingAddress(request.shippingAddress());
        order.setCity(request.city());
        order.setCountry(request.country());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems().values()) {
            Product product = productRepository.findById(cartItem.getProductId()).get();

            // decrement stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // build order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPrice());
            orderItems.add(orderItem);

            total = total.add(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        // clear cart after successful order
        cartService.clearCart(user.getId());

        return orderMapper.toResponse(saved);
    }

    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }

        return orderMapper.toResponse(order);
    }
}