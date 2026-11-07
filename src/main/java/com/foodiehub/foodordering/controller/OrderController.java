package com.foodiehub.foodordering.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.foodiehub.foodordering.entity.*;
import com.foodiehub.foodordering.repository.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    public record PlaceOrderRequest(@NotBlank(message = "Delivery address is required") String address) {}

    private final OrderRepository orders;
    private final CartItemRepository carts;
    private final UserRepository users;

    public OrderController(OrderRepository orders, CartItemRepository carts, UserRepository users) {
        this.orders = orders; this.carts = carts; this.users = users;
    }

    // Checkout: turns the user's cart into an order, then empties the cart
    @PostMapping("/{userId}")
    @Transactional
    public Order placeOrder(@PathVariable Long userId, @Valid @RequestBody PlaceOrderRequest req) {
        User user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        List<CartItem> cart = carts.findByUserId(userId);
        if (cart.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your cart is empty");
        }
        Order order = new Order();
        order.setUser(user);
        order.setAddress(req.address().trim());
        double total = 0;
        for (CartItem c : cart) {
            order.getItems().add(new OrderItem(c.getFood().getName(), c.getFood().getPrice(), c.getQuantity()));
            total += c.getSubtotal();
        }
        order.setTotal(Math.round(total * 100.0) / 100.0);
        Order saved = orders.save(order);
        carts.deleteByUserId(userId);
        return saved;
    }

    @GetMapping("/user/{userId}")
    public List<Order> myOrders(@PathVariable Long userId) {
        return orders.findByUserIdOrderByIdDesc(userId);
    }

    // Admin: list all orders / change status (PLACED, PREPARING, OUT_FOR_DELIVERY, DELIVERED)
    @GetMapping
    public List<Order> all() { return orders.findAll(); }

    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestParam String status) {
        Order o = orders.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        o.setStatus(status.toUpperCase());
        return orders.save(o);
    }
}
