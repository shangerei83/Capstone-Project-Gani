package com.capstone.repository;

import com.capstone.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Category Repository - Persistence Layer
 * 
 * This repository handles data access operations for categories.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find active categories
     * 
     * @return list of active categories
     */
    @Query("SELECT c FROM Category c WHERE c.isActive = true")
    List<Category> findActiveCategories();

    /**
     * Find root categories (no parent)
     * 
     * @return list of root categories
     */
    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL AND c.isActive = true")
    List<Category> findRootCategories();

    /**
     * Find subcategories of a parent category
     * 
     * @param parentId parent category ID
     * @return list of subcategories
     */
    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId AND c.isActive = true")
    List<Category> findByParentId(@Param("parentId") Long parentId);

    /**
     * Find categories by name (case-insensitive)
     * 
     * @param name category name
     * @return list of matching categories
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.isActive = true")
    List<Category> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find categories ordered by display order
     * 
     * @return list of categories ordered by display order
     */
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findActiveCategoriesOrdered();

    /**
     * Find categories with products
     * 
     * @return list of categories that have products
     */
    @Query("SELECT DISTINCT c FROM Category c JOIN c.products p WHERE c.isActive = true AND p.isActive = true")
    List<Category> findCategoriesWithProducts();
}
