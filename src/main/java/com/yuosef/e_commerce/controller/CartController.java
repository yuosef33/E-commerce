package com.yuosef.e_commerce.controller;

import com.yuosef.e_commerce.models.Cart;
import com.yuosef.e_commerce.models.Dtos.ApiResponse;
import com.yuosef.e_commerce.models.Dtos.CartResponse;
import com.yuosef.e_commerce.models.Mappers.CartMapper;
import com.yuosef.e_commerce.models.User;
import com.yuosef.e_commerce.services.Impl.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;


    private Long getCurrentUserId(Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        return user.getId();
    }

    @GetMapping//get cart data
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Principal principal) {
        Cart cart = cartService.getCart(getCurrentUserId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Cart fetched successfully", cartMapper.toResponse(cart)));
    }

    @PostMapping("/items")// add item to cart
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            Principal principal,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        Cart cart = cartService.addItem(getCurrentUserId(principal), productId, quantity);
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", cartMapper.toResponse(cart)));
    }

    @PutMapping("/items/{productId}")//update cart data
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            Principal principal,
            @PathVariable Long productId,
            @RequestParam int quantity) {
        Cart cart = cartService.updateItem(getCurrentUserId(principal), productId, quantity);
        return ResponseEntity.ok(ApiResponse.ok("Cart updated", cartMapper.toResponse(cart)));
    }

    @DeleteMapping("/items/{productId}")//delete item from cart
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            Principal principal,
            @PathVariable Long productId) {
        Cart cart = cartService.removeItem(getCurrentUserId(principal), productId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", cartMapper.toResponse(cart)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(Principal principal) {
        cartService.clearCart(getCurrentUserId(principal));
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared"));
    }
}