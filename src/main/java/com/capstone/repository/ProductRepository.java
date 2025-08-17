package com.capstone.repository;

import com.capstone.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product Repository - Persistence Layer
 * 
 * This repository handles data access operations for products and demonstrates
 * the separation of concerns in the three-tier architecture.
 * 
 * Responsibilities:
 * - Data access operations (CRUD)
 * - Database queries and operations
 * - NO business logic (delegates to service layer)
 * - NO view logic (delegates to controller layer)
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find products by category ID
     * 
     * @param categoryId category ID
     * @return list of products in the specified category
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Search products by title or description
     * 
     * @param query search query
     * @return list of matching products
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchByQuery(@Param("query") String query);

    /**
     * Find featured products (for home page)
     * 
     * @return list of featured products
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    List<Product> findFeaturedProducts();

    /**
     * Find active products
     * 
     * @return list of active products
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true")
    List<Product> findActiveProducts();

    /**
     * Find products by price range
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of products in the specified price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Find products by seller ID
     * 
     * @param sellerId seller ID
     * @return list of products by the specified seller
     */
    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId")
    List<Product> findBySellerId(@Param("sellerId") Long sellerId);

    /**
     * Find products with low stock
     * 
     * @return list of products with low stock
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= 10 AND p.isActive = true")
    List<Product> findLowStockProducts();

    /**
     * Find products by average rating
     * 
     * @param minRating minimum rating
     * @return list of products with rating >= minRating
     */
    @Query("SELECT p FROM Product p WHERE p.averageRating >= :minRating AND p.isActive = true")
    List<Product> findByMinRating(@Param("minRating") BigDecimal minRating);

    /**
     * Find products created in the last N days
     * 
     * @param days number of days
     * @return list of recently created products
     */
    @Query("SELECT p FROM Product p WHERE p.createdAt >= FUNCTION('DATE_SUB', CURRENT_DATE, :days) AND p.isActive = true")
    List<Product> findRecentlyCreated(@Param("days") Integer days);
}
