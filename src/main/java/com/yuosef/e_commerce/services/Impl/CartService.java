package com.yuosef.e_commerce.services.Impl;

import com.yuosef.e_commerce.models.Cart;
import com.yuosef.e_commerce.models.CartItem;
import com.yuosef.e_commerce.models.Product;
import com.yuosef.e_commerce.models.exceptions.ResourceNotFoundException;
import com.yuosef.e_commerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class CartService {

    // userId -> Cart
    private final Map<Long, Cart> store = new ConcurrentHashMap<>();

    private final ProductRepository productRepository;

    // get or create cart for user
    public Cart getCart(Long userId) {
        return store.computeIfAbsent(userId, Cart::new);
    }

    // add or increase quantity
    public Cart addItem(Long userId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        Cart cart = getCart(userId);
        cart.getItems().merge(productId,
                new CartItem(productId, product.getName(), product.getPrice(), quantity),
                (existing, newItem) -> {
                    existing.setQuantity(existing.getQuantity() + quantity);
                    return existing;
                }
        );
        return cart;
    }

    // update quantity directly
    public Cart updateItem(Long userId, Long productId, int quantity) {
        Cart cart = getCart(userId);

        if (!cart.getItems().containsKey(productId)) {
            throw new ResourceNotFoundException("Item not in cart");
        }
        if (quantity <= 0) {
            cart.getItems().remove(productId);
        } else {
            cart.getItems().get(productId).setQuantity(quantity);
        }
        return cart;
    }

    // remove single item
    public Cart removeItem(Long userId, Long productId) {
        Cart cart = getCart(userId);
        cart.getItems().remove(productId);
        return cart;
    }

    // clear entire cart
    public void clearCart(Long userId) {
        store.remove(userId);
    }
}