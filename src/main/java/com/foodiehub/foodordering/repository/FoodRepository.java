package com.foodiehub.foodordering.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.foodiehub.foodordering.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByNameContainingIgnoreCase(String name);

    List<Food> findByCategoryIgnoreCase(String category);

    List<Food> findByCategoryIgnoreCaseAndNameContainingIgnoreCase(String category, String name);

    @Query("SELECT DISTINCT f.category FROM Food f WHERE f.category IS NOT NULL ORDER BY f.category")
    List<String> findAllCategories();
}