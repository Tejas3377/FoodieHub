package com.foodiehub.foodordering.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String foodName;
    private double price;
    private int quantity;

    public OrderItem() {}
    public OrderItem(String foodName, double price, int quantity) {
        this.foodName = foodName; this.price = price; this.quantity = quantity;
    }
    public Long getId() { return id; }
    public String getFoodName() { return foodName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
}
