package com.capstone.service.impl;

import com.capstone.domain.Product;
import com.capstone.repository.ProductRepository;
import com.capstone.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Product Service Implementation - Application Layer
 * 
 * This service implements business logic for products and demonstrates
 * the separation of concerns in the three-tier architecture.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts() {
        return productRepository.findFeaturedProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getActiveProducts();
        }
        return productRepository.searchByQuery(query.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }

    @Override
    public Product createProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        // Validate product data
        validateProduct(product);
        
        // Set default values
        if (product.getStockQuantity() == null) {
            product.setStockQuantity(0);
        }
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
        if (product.getIsFeatured() == null) {
            product.setIsFeatured(false);
        }
        if (product.getAverageRating() == null) {
            product.setAverageRating(BigDecimal.ZERO);
        }
        if (product.getReviewCount() == null) {
            product.setReviewCount(0);
        }
        if (product.getViewCount() == null) {
            product.setViewCount(0);
        }
        
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        Product existingProduct = getProductById(id);
        if (existingProduct == null) {
            throw new IllegalArgumentException("Product with ID " + id + " not found");
        }
        
        // Validate product data
        validateProduct(product);
        
        // Update fields
        existingProduct.setTitle(product.getTitle());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setOriginalPrice(product.getOriginalPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setIsActive(product.getIsActive());
        existingProduct.setIsFeatured(product.getIsFeatured());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setImageUrl(product.getImageUrl());
        
        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        
        Product product = getProductById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + id + " not found");
        }
        
        // Soft delete - mark as inactive instead of removing
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice == null || maxPrice == null) {
            throw new IllegalArgumentException("Price range cannot be null");
        }
        if (minPrice < 0 || maxPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("Min price cannot be greater than max price");
        }
        
        return productRepository.findByPriceRange(
            BigDecimal.valueOf(minPrice), 
            BigDecimal.valueOf(maxPrice)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getActiveProducts() {
        return productRepository.findActiveProducts();
    }

    @Override
    public void incrementViewCount(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        
        Product product = getProductById(productId);
        if (product != null) {
            product.incrementViewCount();
            productRepository.save(product);
        }
    }

    @Override
    public void updateAverageRating(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        
        Product product = getProductById(productId);
        if (product != null) {
            product.updateAverageRating();
            productRepository.save(product);
        }
    }

    // Private helper methods
    private void validateProduct(Product product) {
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Product title is required");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("Product category is required");
        }
        if (product.getSeller() == null) {
            throw new IllegalArgumentException("Product seller is required");
        }
    }
}
