package com.capstone.controller;

import com.capstone.domain.Category;
import com.capstone.domain.Product;
import com.capstone.repository.CategoryRepository;
import com.capstone.repository.ProductRepository;
import com.capstone.service.CategoryService;
import com.capstone.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
 * Catalog Controller Test
 * 
 * This test demonstrates unit testing for the catalog presentation layer
 * and contributes to the required 50% code coverage.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@WebMvcTest(CatalogController.class)
@AutoConfigureDataJpa
class CatalogControllerTest {

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
     * Test catalog page access without parameters
     */
    @Test
    @WithMockUser
    void testCatalogPageWithoutParameters() throws Exception {
        // Mock service methods
        List<Product> activeProducts = new ArrayList<>();
        List<Category> activeCategories = new ArrayList<>();
        when(productService.getActiveProducts()).thenReturn(activeProducts);
        when(categoryService.getActiveCategories()).thenReturn(activeCategories);
        
        mockMvc.perform(get("/catalog"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attribute("title", "Product Catalog"))
                .andExpect(model().attribute("products", activeProducts))
                .andExpect(model().attribute("categories", activeCategories));
    }

    /**
     * Test catalog page with search query
     */
    @Test
    @WithMockUser
    void testCatalogPageWithSearchQuery() throws Exception {
        // Mock service methods
        List<Product> searchResults = new ArrayList<>();
        List<Category> activeCategories = new ArrayList<>();
        when(productService.searchProducts("laptop")).thenReturn(searchResults);
        when(categoryService.getActiveCategories()).thenReturn(activeCategories);
        
        mockMvc.perform(get("/catalog")
                        .param("query", "laptop"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attribute("title", "Product Catalog"))
                .andExpect(model().attribute("products", searchResults))
                .andExpect(model().attribute("categories", activeCategories))
                .andExpect(model().attribute("searchQuery", "laptop"));
    }

    /**
     * Test catalog page with category filter
     */
    @Test
    @WithMockUser
    void testCatalogPageWithCategoryFilter() throws Exception {
        // Mock service methods
        Long categoryId = 1L;
        List<Product> categoryProducts = new ArrayList<>();
        List<Category> activeCategories = new ArrayList<>();
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Test Category");
        
        when(productService.getProductsByCategory(categoryId)).thenReturn(categoryProducts);
        when(categoryService.getActiveCategories()).thenReturn(activeCategories);
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        
        mockMvc.perform(get("/catalog")
                        .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attribute("title", "Product Catalog"))
                .andExpect(model().attribute("products", categoryProducts))
                .andExpect(model().attribute("categories", activeCategories))
                .andExpect(model().attribute("selectedCategory", category));
    }

    /**
     * Test catalog page with sorting
     */
    @Test
    @WithMockUser
    void testCatalogPageWithSorting() throws Exception {
        // Mock service methods
        List<Product> activeProducts = new ArrayList<>();
        List<Category> activeCategories = new ArrayList<>();
        when(productService.getActiveProducts()).thenReturn(activeProducts);
        when(categoryService.getActiveCategories()).thenReturn(activeCategories);
        
        mockMvc.perform(get("/catalog")
                        .param("sort", "price_low"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attribute("title", "Product Catalog"))
                .andExpect(model().attribute("products", activeProducts))
                .andExpect(model().attribute("categories", activeCategories));
    }

    /**
     * Test catalog page with pagination
     */
    @Test
    @WithMockUser
    void testCatalogPageWithPagination() throws Exception {
        // Mock service methods
        List<Product> activeProducts = new ArrayList<>();
        List<Category> activeCategories = new ArrayList<>();
        when(productService.getActiveProducts()).thenReturn(activeProducts);
        when(categoryService.getActiveCategories()).thenReturn(activeCategories);
        
        mockMvc.perform(get("/catalog")
                        .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attribute("title", "Product Catalog"))
                .andExpect(model().attribute("products", activeProducts))
                .andExpect(model().attribute("categories", activeCategories));
    }

    /**
     * Test search products endpoint
     */
    @Test
    @WithMockUser
    void testSearchProducts() throws Exception {
        // Mock service methods
        List<Product> searchResults = new ArrayList<>();
        List<Category> activeCategories = new ArrayList<>();
        when(productService.searchProducts("smartphone")).thenReturn(searchResults);
        when(categoryService.getActiveCategories()).thenReturn(activeCategories);
        
        mockMvc.perform(get("/catalog")
                        .param("query", "smartphone"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attribute("title", "Product Catalog"))
                .andExpect(model().attribute("products", searchResults))
                .andExpect(model().attribute("categories", activeCategories))
                .andExpect(model().attribute("searchQuery", "smartphone"));
    }

    /**
     * Test search products with pagination
     */
    @Test
    @WithMockUser
    void testSearchProductsWithPagination() throws Exception {
        // Mock service methods
        List<Product> searchResults = new ArrayList<>();
        List<Category> activeCategories = new ArrayList<>();
        when(productService.searchProducts("smartphone")).thenReturn(searchResults);
        when(categoryService.getActiveCategories()).thenReturn(activeCategories);
        
        mockMvc.perform(get("/catalog")
                        .param("query", "smartphone")
                        .param("page", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attribute("searchQuery", "smartphone"));
    }
}
