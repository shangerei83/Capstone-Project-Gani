package com.capstone.controller;

import com.capstone.service.CategoryService;
import com.capstone.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home Controller - Presentation Layer
 * 
 * This controller handles the main page requests and demonstrates
 * the separation of concerns in the three-tier architecture.
 * 
 * Responsibilities:
 * - Handle HTTP requests for home page
 * - Prepare model data for Thymeleaf templates
 * - Delegate business logic to service layer
 * - Return appropriate view names
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Controller
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Autowired
    public HomeController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * Display the home page
     * 
     * @param model Spring MVC model for template data
     * @return view name for home page
     */
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // Set page title
        model.addAttribute("title", "Home");
        
        // Get featured products and categories from service layer
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        model.addAttribute("categories", categoryService.getActiveCategories());
        
        return "home";
    }

    /**
     * Display the about page
     * 
     * @param model Spring MVC model for template data
     * @return view name for about page
     */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About");
        return "about";
    }

    /**
     * Display the contact page
     * 
     * @param model Spring MVC model for template data
     * @return view name for contact page
     */
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("title", "Contact");
        return "contact";
    }
}
