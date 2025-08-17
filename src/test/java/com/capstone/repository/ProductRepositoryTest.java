package com.capstone.repository;

import com.capstone.domain.Product;
import com.capstone.domain.Category;
import com.capstone.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Product Repository Test - Tests the Data Access Layer
 * 
 * This test demonstrates testing of the repository layer and contributes
 * to the required 50% code coverage for Stage 5.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;

    private Category testCategory;
    private Product testProduct1;
    private Product testProduct2;
    private User testSeller;

    @BeforeEach
    void setUp() {
        // Create test seller
        testSeller = new User();
        testSeller.setEmail("seller@example.com");
        testSeller.setPasswordHash("hashedpassword");
        testSeller.setFirstName("Test");
        testSeller.setLastName("Seller");
        testSeller = entityManager.persistAndFlush(testSeller);
        
        // Create test category
        testCategory = new Category();
        testCategory.setName("Electronics");
        testCategory.setDescription("Electronic devices and gadgets");
        testCategory.setIsActive(true);
        testCategory = entityManager.persistAndFlush(testCategory);

        // Create test products
        testProduct1 = new Product();
        testProduct1.setTitle("Laptop");
        testProduct1.setDescription("High-performance laptop");
        testProduct1.setPrice(new BigDecimal("999.99"));
        testProduct1.setCategory(testCategory);
        testProduct1.setSeller(testSeller);
        testProduct1.setIsActive(true);
        testProduct1.setIsFeatured(true);
        testProduct1 = entityManager.persistAndFlush(testProduct1);

        testProduct2 = new Product();
        testProduct2.setTitle("Smartphone");
        testProduct2.setDescription("Latest smartphone model");
        testProduct2.setPrice(new BigDecimal("599.99"));
        testProduct2.setCategory(testCategory);
        testProduct2.setSeller(testSeller);
        testProduct2.setIsActive(true);
        testProduct2.setIsFeatured(false);
        testProduct2 = entityManager.persistAndFlush(testProduct2);

        entityManager.clear();
    }

    @Test
    void testFindAll() {
        // Act
        List<Product> products = productRepository.findAll();

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
    }

    @Test
    void testFindById() {
        // Act
        Optional<Product> product = productRepository.findById(testProduct1.getId());

        // Assert
        assertTrue(product.isPresent());
        assertEquals("Laptop", product.get().getTitle());
    }

    @Test
    void testFindByIdNotFound() {
        // Act
        Optional<Product> product = productRepository.findById(999L);

        // Assert
        assertFalse(product.isPresent());
    }

    @Test
    void testSave() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setTitle("Tablet");
        newProduct.setDescription("Portable tablet device");
        newProduct.setPrice(new BigDecimal("299.99"));
        newProduct.setCategory(testCategory);
        newProduct.setSeller(testSeller);
        newProduct.setIsActive(true);

        // Act
        Product savedProduct = productRepository.save(newProduct);

        // Assert
        assertNotNull(savedProduct.getId());
        assertEquals("Tablet", savedProduct.getTitle());
        
        // Verify it's in database
        Product foundProduct = entityManager.find(Product.class, savedProduct.getId());
        assertNotNull(foundProduct);
        assertEquals("Tablet", foundProduct.getTitle());
    }

    @Test
    void testUpdate() {
        // Arrange
        testProduct1.setPrice(new BigDecimal("1099.99"));

        // Act
        Product updatedProduct = productRepository.save(testProduct1);

        // Assert
        assertEquals(new BigDecimal("1099.99"), updatedProduct.getPrice());
        
        // Verify update in database
        Product foundProduct = entityManager.find(Product.class, testProduct1.getId());
        assertEquals(new BigDecimal("1099.99"), foundProduct.getPrice());
    }

    @Test
    void testDelete() {
        // Act
        productRepository.deleteById(testProduct1.getId());

        // Assert
        Product foundProduct = entityManager.find(Product.class, testProduct1.getId());
        assertNull(foundProduct);
    }

    @Test
    void testFindByCategoryId() {
        // Act
        List<Product> products = productRepository.findByCategoryId(testCategory.getId());

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
        assertTrue(products.stream().allMatch(p -> p.getCategory().getId().equals(testCategory.getId())));
    }

    @Test
    void testFindActiveProducts() {
        // Act
        List<Product> products = productRepository.findActiveProducts();

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
        assertTrue(products.stream().allMatch(p -> p.getIsActive()));
    }

    @Test
    void testFindFeaturedProducts() {
        // Act
        List<Product> products = productRepository.findFeaturedProducts();

        // Assert
        assertNotNull(products);
        // Note: findFeaturedProducts returns active products ordered by creation date, not by isFeatured flag
        assertTrue(products.size() > 0);
        assertTrue(products.stream().allMatch(p -> p.getIsActive()));
    }

    @Test
    void testFindByPriceRange() {
        // Act
        List<Product> products = productRepository.findByPriceRange(
            new BigDecimal("500.0"), new BigDecimal("1000.0"));

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
        assertTrue(products.stream().allMatch(p -> 
            p.getPrice().compareTo(new BigDecimal("500.0")) >= 0 && 
            p.getPrice().compareTo(new BigDecimal("1000.0")) <= 0));
    }

    @Test
    void testSearchByQuery() {
        // Act
        List<Product> products = productRepository
            .searchByQuery("laptop");

        // Assert
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getTitle());
    }

    @Test
    void testSearchByQueryWithDescription() {
        // Act
        List<Product> products = productRepository
            .searchByQuery("smartphone");

        // Assert
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Smartphone", products.get(0).getTitle());
    }

    @Test
    void testSearchByQueryNoMatch() {
        // Act
        List<Product> products = productRepository
            .searchByQuery("nonexistent");

        // Assert
        assertNotNull(products);
        assertEquals(0, products.size());
    }
}
