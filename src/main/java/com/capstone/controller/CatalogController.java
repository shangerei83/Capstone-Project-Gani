package com.capstone.controller;

import com.capstone.domain.Category;
import com.capstone.domain.Product;
import com.capstone.service.CategoryService;
import com.capstone.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Catalog Controller - Presentation Layer
 * 
 * This controller handles product catalog requests and demonstrates
 * the separation of concerns in the three-tier architecture.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Controller
public class CatalogController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Autowired
    public CatalogController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * Display the product catalog
     * 
     * @param model Spring MVC model for template data
     * @param query search query
     * @param categoryId category filter
     * @param minPrice minimum price filter
     * @param maxPrice maximum price filter
     * @return view name for catalog page
     */
    @GetMapping("/catalog")
    public String catalog(Model model,
                         @RequestParam(required = false) String query,
                         @RequestParam(required = false) Long categoryId,
                         @RequestParam(required = false) Double minPrice,
                         @RequestParam(required = false) Double maxPrice) {
        
        System.out.println("=== Catalog Controller Called ===");
        System.out.println("Query: " + query);
        System.out.println("CategoryId: " + categoryId);
        System.out.println("MinPrice: " + minPrice);
        System.out.println("MaxPrice: " + maxPrice);
        
        model.addAttribute("title", "Product Catalog");
        
        // Get products based on filters
        List<Product> products;
        if (query != null && !query.trim().isEmpty()) {
            products = productService.searchProducts(query);
            System.out.println("Search products result: " + (products != null ? products.size() : "null"));
            model.addAttribute("products", products);
            model.addAttribute("searchQuery", query);
        } else if (categoryId != null) {
            products = productService.getProductsByCategory(categoryId);
            System.out.println("Category products result: " + (products != null ? products.size() : "null"));
            model.addAttribute("products", products);
            model.addAttribute("selectedCategory", categoryService.getCategoryById(categoryId));
        } else if (minPrice != null || maxPrice != null) {
            products = productService.getProductsByPriceRange(
                minPrice != null ? minPrice : 0.0,
                maxPrice != null ? maxPrice : Double.MAX_VALUE
            );
            System.out.println("Price range products result: " + (products != null ? products.size() : "null"));
            model.addAttribute("products", products);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
        } else {
            products = productService.getActiveProducts();
            System.out.println("Active products result: " + (products != null ? products.size() : "null"));
            model.addAttribute("products", products);
        }
        
        // Get categories for sidebar
        List<Category> categories = categoryService.getActiveCategories();
        System.out.println("Categories result: " + (categories != null ? categories.size() : "null"));
        model.addAttribute("categories", categories);
        
        System.out.println("=== Catalog Controller Finished ===");
        
        return "catalog";
    }

    /**
     * Display products by category
     * 
     * @param categoryId category ID
     * @param model Spring MVC model for template data
     * @return view name for category page
     */
    @GetMapping("/catalog/category/{categoryId}")
    public String categoryProducts(@PathVariable Long categoryId, Model model) {
        model.addAttribute("title", "Category Products");
        model.addAttribute("products", productService.getProductsByCategory(categoryId));
        model.addAttribute("selectedCategory", categoryService.getCategoryById(categoryId));
        model.addAttribute("categories", categoryService.getActiveCategories());
        
        return "catalog";
    }

    /**
     * Display product details
     * 
     * @param productId product ID
     * @param model Spring MVC model for template data
     * @return view name for product details page
     */
    @GetMapping("/product/{productId}")
    public String productDetails(@PathVariable Long productId, Model model) {
        model.addAttribute("title", "Product Details");
        model.addAttribute("product", productService.getProductById(productId));
        
        // Increment view count
        productService.incrementViewCount(productId);
        
        return "product/details";
    }
}
