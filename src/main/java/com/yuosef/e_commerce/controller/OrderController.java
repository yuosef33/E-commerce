package com.yuosef.e_commerce.controller;

import com.yuosef.e_commerce.models.Dtos.ApiResponse;
import com.yuosef.e_commerce.models.Dtos.CheckoutRequest;
import com.yuosef.e_commerce.models.Dtos.OrderResponse;
import com.yuosef.e_commerce.models.User;
import com.yuosef.e_commerce.services.Impl.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private User getCurrentUser(Principal principal) {
        return (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
    }

    @PostMapping("/checkout")// checkout all the cart
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            Principal principal,
            @Valid @RequestBody CheckoutRequest request) {
        OrderResponse order = orderService.checkout(getCurrentUser(principal), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order placed successfully", order));
    }

    @GetMapping//get all user orders
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(Principal principal) {
        List<OrderResponse> orders = orderService.getMyOrders(getCurrentUser(principal).getId());
        return ResponseEntity.ok(ApiResponse.ok("Orders fetched successfully", orders));
    }

    @GetMapping("/{id}")//get order by id
    public ResponseEntity<ApiResponse<OrderResponse>> getById(
            Principal principal,
            @PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id, getCurrentUser(principal).getId());
        return ResponseEntity.ok(ApiResponse.ok("Order fetched successfully", order));
    }
}