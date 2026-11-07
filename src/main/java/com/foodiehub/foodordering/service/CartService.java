package com.foodiehub.foodordering.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.foodiehub.foodordering.dto.CartResponse;
import com.foodiehub.foodordering.entity.CartItem;
import com.foodiehub.foodordering.entity.Food;
import com.foodiehub.foodordering.entity.User;
import com.foodiehub.foodordering.repository.CartItemRepository;
import com.foodiehub.foodordering.repository.FoodRepository;
import com.foodiehub.foodordering.repository.UserRepository;

@Service
public class CartService {

    private static final int MAX_QUANTITY = 20;

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    public CartService(CartItemRepository cartItemRepository,
                       UserRepository userRepository,
                       FoodRepository foodRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
    }

    public CartResponse getCart(Long userId) {
        checkUserExists(userId);
        return buildResponse(userId);
    }

    @Transactional
    public CartResponse addItem(Long userId, Long foodId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found"));

        if (!food.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This food is currently unavailable");
        }

        CartItem item = cartItemRepository.findByUserIdAndFoodId(userId, foodId)
                .orElse(null);

        if (item == null) {
            item = new CartItem(user, food, quantity);
        } else {
            item.setQuantity(Math.min(item.getQuantity() + quantity, MAX_QUANTITY));
        }

        cartItemRepository.save(item);
        return buildResponse(userId);
    }

    @Transactional
    public CartResponse updateQuantity(Long userId, Long itemId, int quantity) {
        CartItem item = findOwnedItem(userId, itemId);
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return buildResponse(userId);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        CartItem item = findOwnedItem(userId, itemId);
        cartItemRepository.delete(item);
        return buildResponse(userId);
    }

    @Transactional
    public void clearCart(Long userId) {
        checkUserExists(userId);
        cartItemRepository.deleteByUserId(userId);
    }

    // ---------- helper methods ----------

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    private CartItem findOwnedItem(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        // Make sure a user can only touch their own cart items
        if (!item.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found");
        }
        return item;
    }

    private CartResponse buildResponse(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);

        int itemCount = items.stream().mapToInt(CartItem::getQuantity).sum();
        double total = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        total = Math.round(total * 100.0) / 100.0;

        return new CartResponse(items, itemCount, total);
    }
}