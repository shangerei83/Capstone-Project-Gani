package com.capstone.service;

import com.capstone.domain.Category;
import java.util.List;

/**
 * Category Service - Application Layer
 * 
 * This service handles business logic related to product categories.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
public interface CategoryService {

    /**
     * Get all categories
     * 
     * @return list of all categories
     */
    List<Category> getAllCategories();

    /**
     * Get active categories only
     * 
     * @return list of active categories
     */
    List<Category> getActiveCategories();

    /**
     * Get category by ID
     * 
     * @param id category ID
     * @return category or null if not found
     */
    Category getCategoryById(Long id);

    /**
     * Create new category
     * 
     * @param category category data
     * @return created category
     */
    Category createCategory(Category category);

    /**
     * Update existing category
     * 
     * @param id category ID
     * @param category updated category data
     * @return updated category
     */
    Category updateCategory(Long id, Category category);

    /**
     * Delete category
     * 
     * @param id category ID
     */
    void deleteCategory(Long id);

    /**
     * Get root categories (no parent)
     * 
     * @return list of root categories
     */
    List<Category> getRootCategories();

    /**
     * Get subcategories of a parent category
     * 
     * @param parentId parent category ID
     * @return list of subcategories
     */
    List<Category> getSubCategories(Long parentId);

    /**
     * Get category hierarchy
     * 
     * @return list of categories with their hierarchy
     */
    List<Category> getCategoryHierarchy();
}
