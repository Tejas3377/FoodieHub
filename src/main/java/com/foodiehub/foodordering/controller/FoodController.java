
package com.foodiehub.foodordering.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.foodiehub.foodordering.entity.Food;
import com.foodiehub.foodordering.repository.FoodRepository;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodRepository foodRepository;

    public FoodController(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    // 1. Get all food items (optional search and category filters)
    @GetMapping
    public List<Food> getAllFoods(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasCategory = category != null && !category.isBlank();

        if (hasSearch && hasCategory) {
            return foodRepository.findByCategoryIgnoreCaseAndNameContainingIgnoreCase(
                    category.trim(), search.trim());
        }
        if (hasSearch) {
            return foodRepository.findByNameContainingIgnoreCase(search.trim());
        }
        if (hasCategory) {
            return foodRepository.findByCategoryIgnoreCase(category.trim());
        }
        return foodRepository.findAll();
    }

    // 1b. Get the list of categories
    @GetMapping("/categories")
    public List<String> getCategories() {
        return foodRepository.findAllCategories();
    }

    // Add a new food item
    @PostMapping
    public Food addFood(@RequestBody Food food) {
        return foodRepository.save(food);
    }
}
