package com.capstone.service;

import com.capstone.domain.Product;
import com.capstone.domain.Category;
import com.capstone.domain.User;
import com.capstone.repository.ProductRepository;
import com.capstone.repository.CategoryRepository;
import com.capstone.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Product Service Test - Tests the Application Layer
 * 
 * This test demonstrates testing of the service layer and contributes
 * to the required 50% code coverage for Stage 5.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private Category testCategory;
    private User testSeller;

    @BeforeEach
    void setUp() {
        // Create test category
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");
        testCategory.setDescription("Electronic devices and gadgets");

        // Create test seller
        testSeller = new User();
        testSeller.setId(1L);
        testSeller.setEmail("seller@example.com");
        testSeller.setFirstName("Test");
        testSeller.setLastName("Seller");

        // Create test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setTitle("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setCategory(testCategory);
        testProduct.setSeller(testSeller);
        testProduct.setIsActive(true);
    }

    @Test
    void testGetAllProducts() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.getAllProducts();

        // Assert
        assertNotNull(actualProducts);
        assertEquals(1, actualProducts.size());
        assertEquals(testProduct.getTitle(), actualProducts.get(0).getTitle());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetFeaturedProducts() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findFeaturedProducts()).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.getFeaturedProducts();

        // Assert
        assertNotNull(actualProducts);
        assertEquals(1, actualProducts.size());
        assertEquals(testProduct.getTitle(), actualProducts.get(0).getTitle());
        verify(productRepository, times(1)).findFeaturedProducts();
    }

    @Test
    void testGetProductsByCategory() {
        // Arrange
        Long categoryId = 1L;
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findByCategoryId(categoryId)).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.getProductsByCategory(categoryId);

        // Assert
        assertNotNull(actualProducts);
        assertEquals(1, actualProducts.size());
        assertEquals(testProduct.getTitle(), actualProducts.get(0).getTitle());
        verify(productRepository, times(1)).findByCategoryId(categoryId);
    }

    @Test
    void testSearchProducts() {
        // Arrange
        String query = "test";
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.searchByQuery(query))
            .thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.searchProducts(query);

        // Assert
        assertNotNull(actualProducts);
        assertEquals(1, actualProducts.size());
        assertEquals(testProduct.getTitle(), actualProducts.get(0).getTitle());
        verify(productRepository, times(1))
            .searchByQuery(query);
    }

    @Test
    void testGetProductById() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // Act
        Product actualProduct = productService.getProductById(productId);

        // Assert
        assertNotNull(actualProduct);
        assertEquals(testProduct.getTitle(), actualProduct.getTitle());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testGetProductByIdNotFound() {
        // Arrange
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        Product actualProduct = productService.getProductById(productId);

        // Assert
        assertNull(actualProduct);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testCreateProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Product actualProduct = productService.createProduct(testProduct);

        // Assert
        assertNotNull(actualProduct);
        assertEquals(testProduct.getTitle(), actualProduct.getTitle());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void testUpdateProduct() {
        // Arrange
        Long productId = 1L;
        Product updatedProduct = new Product();
        updatedProduct.setTitle("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(new BigDecimal("149.99"));
        updatedProduct.setCategory(testCategory);
        updatedProduct.setSeller(testSeller);

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product actualProduct = productService.updateProduct(productId, updatedProduct);

        // Assert
        assertNotNull(actualProduct);
        assertEquals("Updated Product", actualProduct.getTitle());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() {
        // Arrange
        Long productId = 999L;
        Product updatedProduct = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(productId, updatedProduct);
        });
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(testProduct);
        assertFalse(testProduct.getIsActive());
    }

    @Test
    void testGetProductsByPriceRange() {
        // Arrange
        Double minPrice = 50.0;
        Double maxPrice = 150.0;
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findByPriceRange(new BigDecimal(minPrice.toString()), new BigDecimal(maxPrice.toString()))).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.getProductsByPriceRange(minPrice, maxPrice);

        // Assert
        assertNotNull(actualProducts);
        assertEquals(1, actualProducts.size());
        assertEquals(testProduct.getTitle(), actualProducts.get(0).getTitle());
        verify(productRepository, times(1)).findByPriceRange(new BigDecimal(minPrice.toString()), new BigDecimal(maxPrice.toString()));
    }

    @Test
    void testGetActiveProducts() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findActiveProducts()).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.getActiveProducts();

        // Assert
        assertNotNull(actualProducts);
        assertEquals(1, actualProducts.size());
        assertEquals(testProduct.getTitle(), actualProducts.get(0).getTitle());
        verify(productRepository, times(1)).findActiveProducts();
    }
}
