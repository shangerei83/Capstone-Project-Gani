package com.capstone.service.impl;

import com.capstone.domain.Category;
import com.capstone.repository.CategoryRepository;
import com.capstone.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Category Service Implementation - Application Layer
 * 
 * This service implements business logic for categories.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findActiveCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.orElse(null);
    }

    @Override
    public Category createCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        // Validate category data
        validateCategory(category);
        
        // Set default values
        if (category.getIsActive() == null) {
            category.setIsActive(true);
        }
        if (category.getDisplayOrder() == null) {
            category.setDisplayOrder(0);
        }
        
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        if (id == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        Category existingCategory = getCategoryById(id);
        if (existingCategory == null) {
            throw new IllegalArgumentException("Category with ID " + id + " not found");
        }
        
        // Validate category data
        validateCategory(category);
        
        // Update fields
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setImageUrl(category.getImageUrl());
        existingCategory.setIsActive(category.getIsActive());
        existingCategory.setDisplayOrder(category.getDisplayOrder());
        existingCategory.setParentCategory(category.getParentCategory());
        
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        
        Category category = getCategoryById(id);
        if (category == null) {
            throw new IllegalArgumentException("Category with ID " + id + " not found");
        }
        
        // Check if category has products
        if (category.hasProducts()) {
            throw new IllegalStateException("Cannot delete category with products. Remove products first.");
        }
        
        // Check if category has subcategories
        if (category.hasSubCategories()) {
            throw new IllegalStateException("Cannot delete category with subcategories. Remove subcategories first.");
        }
        
        // Soft delete - mark as inactive instead of removing
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return categoryRepository.findRootCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getSubCategories(Long parentId) {
        if (parentId == null) {
            throw new IllegalArgumentException("Parent category ID cannot be null");
        }
        return categoryRepository.findByParentId(parentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoryHierarchy() {
        return categoryRepository.findActiveCategoriesOrdered();
    }

    // Private helper methods
    private void validateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }
        
        // Check for circular references in parent category
        if (category.getParentCategory() != null) {
            Category parent = category.getParentCategory();
            if (parent.getId() != null && parent.getId().equals(category.getId())) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }
            
            // Check for deeper circular references
            Category current = parent;
            while (current.getParentCategory() != null) {
                if (current.getParentCategory().getId().equals(category.getId())) {
                    throw new IllegalArgumentException("Circular reference detected in category hierarchy");
                }
                current = current.getParentCategory();
            }
        }
    }
}
