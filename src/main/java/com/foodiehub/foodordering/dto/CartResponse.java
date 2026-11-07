package com.foodiehub.foodordering.dto;

import java.util.List;

import com.foodiehub.foodordering.entity.CartItem;

public record CartResponse(List<CartItem> items, int itemCount, double total) {
}