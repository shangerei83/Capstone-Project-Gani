package com.capstone.service;

import com.capstone.domain.Product;
import java.util.List;

/**
 * Product Service - Application Layer
 * 
 * This service handles business logic related to products and demonstrates
 * the separation of concerns in the three-tier architecture.
 * 
 * Responsibilities:
 * - Business logic for product operations
 * - Data processing and validation
 * - Coordination between different business processes
 * - NO direct database access (delegates to repository layer)
 * - NO view logic (delegates to controller layer)
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
public interface ProductService {

    /**
     * Get all products
     * 
     * @return list of all products
     */
    List<Product> getAllProducts();

    /**
     * Get featured products for home page
     * 
     * @return list of featured products
     */
    List<Product> getFeaturedProducts();

    /**
     * Get products by category
     * 
     * @param categoryId category ID
     * @return list of products in the specified category
     */
    List<Product> getProductsByCategory(Long categoryId);

    /**
     * Search products by query
     * 
     * @param query search query
     * @return list of matching products
     */
    List<Product> searchProducts(String query);

    /**
     * Get product by ID
     * 
     * @param id product ID
     * @return product or null if not found
     */
    Product getProductById(Long id);

    /**
     * Create new product
     * 
     * @param product product data
     * @return created product
     */
    Product createProduct(Product product);

    /**
     * Update existing product
     * 
     * @param id product ID
     * @param product updated product data
     * @return updated product
     */
    Product updateProduct(Long id, Product product);

    /**
     * Delete product
     * 
     * @param id product ID
     */
    void deleteProduct(Long id);

    /**
     * Get products by price range
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of products in the specified price range
     */
    List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice);

    /**
     * Get active products only
     * 
     * @return list of active products
     */
    List<Product> getActiveProducts();

    /**
     * Increment product view count
     * 
     * @param productId product ID
     */
    void incrementViewCount(Long productId);

    /**
     * Update product average rating
     * 
     * @param productId product ID
     */
    void updateAverageRating(Long productId);
}
