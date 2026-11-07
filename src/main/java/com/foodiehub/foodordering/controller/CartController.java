package com.foodiehub.foodordering.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.foodiehub.foodordering.dto.AddToCartRequest;
import com.foodiehub.foodordering.dto.CartResponse;
import com.foodiehub.foodordering.dto.UpdateQuantityRequest;
import com.foodiehub.foodordering.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public CartResponse getCart(@PathVariable Long userId) {
        return cartService.getCart(userId);
    }

    @PostMapping("/{userId}/items")
    public CartResponse addItem(@PathVariable Long userId,
                                @Valid @RequestBody AddToCartRequest request) {
        return cartService.addItem(userId, request.foodId(), request.quantity());
    }

    @PutMapping("/{userId}/items/{itemId}")
    public CartResponse updateQuantity(@PathVariable Long userId,
                                       @PathVariable Long itemId,
                                       @Valid @RequestBody UpdateQuantityRequest request) {
        return cartService.updateQuantity(userId, itemId, request.quantity());
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public CartResponse removeItem(@PathVariable Long userId, @PathVariable Long itemId) {
        return cartService.removeItem(userId, itemId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}