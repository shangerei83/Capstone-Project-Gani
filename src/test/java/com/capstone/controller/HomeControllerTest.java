package com.capstone.controller;

import com.capstone.domain.Category;
import com.capstone.domain.Product;
import com.capstone.repository.CategoryRepository;
import com.capstone.repository.ProductRepository;
import com.capstone.service.CategoryService;
import com.capstone.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Home Controller Test
 * 
 * This test demonstrates unit testing for the presentation layer
 * and contributes to the required 50% code coverage.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@WebMvcTest(HomeController.class)
@AutoConfigureDataJpa
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @MockBean
    private CategoryService categoryService;
    
    @MockBean
    private ProductRepository productRepository;
    
    @MockBean
    private CategoryRepository categoryRepository;

    /**
     * Test home page access
     */
    @Test
    @WithMockUser
    void testHomePage() throws Exception {
        // Подготовка моков
        List<Product> featuredProducts = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        
        when(productService.getFeaturedProducts()).thenReturn(featuredProducts);
        when(categoryService.getActiveCategories()).thenReturn(categories);
        
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("title", "Home"))
                .andExpect(model().attribute("featuredProducts", featuredProducts))
                .andExpect(model().attribute("categories", categories));
    }

    /**
     * Test home page with /home path
     */
    @Test
    @WithMockUser
    void testHomePageWithHomePath() throws Exception {
        // Подготовка моков
        List<Product> featuredProducts = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        
        when(productService.getFeaturedProducts()).thenReturn(featuredProducts);
        when(categoryService.getActiveCategories()).thenReturn(categories);
        
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("title", "Home"))
                .andExpect(model().attribute("featuredProducts", featuredProducts))
                .andExpect(model().attribute("categories", categories));
    }

    /**
     * Test about page access
     */
    @Test
    @WithMockUser
    void testAboutPage() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"))
                .andExpect(model().attribute("title", "About"));
    }

    /**
     * Test contact page access
     */
    @Test
    @WithMockUser
    void testContactPage() throws Exception {
        mockMvc.perform(get("/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact"))
                .andExpect(model().attribute("title", "Contact"));
    }
}
