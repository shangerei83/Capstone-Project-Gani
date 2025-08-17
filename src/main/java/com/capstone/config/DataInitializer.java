package com.capstone.config;

import com.capstone.domain.*;
import com.capstone.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Data Initializer - Populates database with test data
 * 
 * This component runs after the application starts and populates
 * the database with sample data for testing and demonstration.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final AddressRepository addressRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                          RoleRepository roleRepository,
                          UserRoleRepository userRoleRepository,
                          CategoryRepository categoryRepository,
                          ProductRepository productRepository,
                          InventoryRepository inventoryRepository,
                          AddressRepository addressRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Initializing Test Data ===");
        
        // Create roles
        Role customerRole = createRole("CUSTOMER", "Customer role for regular users");
        Role sellerRole = createRole("SELLER", "Seller role for product vendors");
        Role adminRole = createRole("ADMIN", "Administrator role");
        
        // Create users
        User customer = createUser("john.doe@example.com", "password123", "John", "Doe", "+1234567890");
        User seller = createUser("jane.smith@example.com", "password123", "Jane", "Smith", "+1234567891");
        User admin = createUser("admin@ganimart.com", "admin123", "Admin", "User", "+1234567892");
        
        // Create test user with known credentials
        User testUser = createUser("ganishah@mail.ru", "password123", "Gani", "Shah", "+1234567893");
        
        // Assign roles
        assignRole(customer, customerRole);
        assignRole(seller, sellerRole);
        assignRole(admin, adminRole);
        assignRole(admin, customerRole); // Admin also has customer role
        assignRole(testUser, customerRole); // Test user has customer role
        
        // Create categories
        Category electronics = createCategory("Electronics", "Electronic devices and gadgets", null);
        Category clothing = createCategory("Clothing", "Fashion and apparel", null);
        Category books = createCategory("Books", "Books and publications", null);
        
        Category smartphones = createCategory("Smartphones", "Mobile phones and accessories", electronics);
        Category laptops = createCategory("Laptops", "Portable computers", electronics);
        Category tshirts = createCategory("T-Shirts", "Casual t-shirts", clothing);
        Category jeansCategory = createCategory("Jeans", "Denim pants", clothing);
        Category fiction = createCategory("Fiction", "Fictional literature", books);
        Category nonFiction = createCategory("Non-Fiction", "Educational and reference books", books);
        
        // Create products
        Product iphone = createProduct("iPhone 15 Pro", 
            "Latest iPhone with advanced camera and performance", 
            new BigDecimal("999.99"), 
            new BigDecimal("1099.99"), 
            seller, 
            smartphones, 
            "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop");
        
        Product macbook = createProduct("MacBook Air M2", 
            "Lightweight laptop with M2 chip", 
            new BigDecimal("1199.99"), 
            null, 
            seller, 
            laptops, 
            "https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=400&h=400&fit=crop");
        
        Product tshirt = createProduct("Cotton T-Shirt", 
            "Comfortable cotton t-shirt in various colors", 
            new BigDecimal("24.99"), 
            new BigDecimal("29.99"), 
            seller, 
            tshirts, 
            "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400&h=400&fit=crop");
        
        Product jeans = createProduct("Slim Fit Jeans", 
            "Modern slim fit jeans for men and women", 
            new BigDecimal("59.99"), 
            null, 
            seller, 
            jeansCategory, 
            "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400&h=400&fit=crop");
        
        Product harryPotter = createProduct("Harry Potter and the Sorcerer's Stone", 
            "First book in the Harry Potter series", 
            new BigDecimal("14.99"), 
            null, 
            seller, 
            fiction, 
            "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=400&fit=crop");
        
        Product javaBook = createProduct("Effective Java", 
            "Comprehensive guide to Java programming", 
            new BigDecimal("39.99"), 
            null, 
            seller, 
            nonFiction, 
            "https://images.unsplash.com/photo-1517842645767-c639042777db?w=400&h=400&fit=crop");
        
        // Create inventory
        createInventory(iphone, 50);
        createInventory(macbook, 25);
        createInventory(tshirt, 100);
        createInventory(jeans, 75);
        createInventory(harryPotter, 200);
        createInventory(javaBook, 150);
        
        // Create addresses
        createAddress(customer, "123 Main St", "Apt 4B", "New York", "NY", "10001", "USA", Address.AddressType.SHIPPING);
        createAddress(customer, "123 Main St", "Apt 4B", "New York", "NY", "10001", "USA", Address.AddressType.BILLING);
        createAddress(seller, "456 Business Ave", "Suite 100", "Los Angeles", "CA", "90210", "USA", Address.AddressType.SHIPPING);
        createAddress(testUser, "789 Test Street", "Apt 5", "Test City", "TC", "12345", "Test Country", Address.AddressType.SHIPPING);
        
        System.out.println("=== Test Data Initialization Complete ===");
        System.out.println("Created:");
        System.out.println("- 4 Users (Customer, Seller, Admin, Test User)");
        System.out.println("- 3 Roles (Customer, Seller, Admin)");
        System.out.println("- 6 Categories (Electronics, Clothing, Books with subcategories)");
        System.out.println("- 6 Products with images and pricing");
        System.out.println("- Inventory records for all products");
        System.out.println("- Sample addresses for users");
        System.out.println();
        System.out.println("=== Test Login Credentials ===");
        System.out.println("Email: ganishah@mail.ru");
        System.out.println("Password: password123");
        System.out.println("================================");
    }
    
    private Role createRole(String name, String description) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setIsActive(true);
        return roleRepository.save(role);
    }
    
    private User createUser(String email, String password, String firstName, String lastName, String phone) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password)); // Encode password with BCrypt
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    private void assignRole(User user, Role role) {
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedBy(user); // Self-assigned for demo
        userRole.setAssignedAt(LocalDateTime.now());
        userRole.setIsActive(true);
        userRoleRepository.save(userRole);
    }
    
    private Category createCategory(String name, String description, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setParentCategory(parent);
        category.setIsActive(true);
        category.setDisplayOrder(0);
        category.setImageUrl("https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=200&h=200&fit=crop");
        return categoryRepository.save(category);
    }
    
    private Product createProduct(String title, String description, BigDecimal price, 
                                BigDecimal originalPrice, User seller, Category category, String imageUrl) {
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setOriginalPrice(originalPrice);
        product.setSeller(seller);
        product.setCategory(category);
        product.setImageUrl(imageUrl);
        product.setIsActive(true);
        product.setIsFeatured(Math.random() < 0.3); // 30% chance to be featured
        product.setAverageRating(BigDecimal.valueOf(4.0 + Math.random() * 1.0)); // 4.0-5.0 rating
        product.setReviewCount((int) (Math.random() * 50) + 5); // 5-55 reviews
        product.setViewCount((int) (Math.random() * 1000) + 100); // 100-1100 views
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
    
    private Inventory createInventory(Product product, int quantity) {
        Inventory inventory = new Inventory(product, quantity);
        inventory.setMinimumStock(10);
        inventory.setReorderPoint(10);
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
        // Update product stock quantity
        product.setStockQuantity(quantity);
        productRepository.save(product);
        return inventoryRepository.save(inventory);
    }
    
    private Address createAddress(User user, String street, String apartment, 
                                String city, String state, String zipCode, String country, Address.AddressType type) {
        Address address = new Address();
        address.setUser(user);
        address.setStreetAddress(street + (apartment != null ? ", " + apartment : ""));
        address.setCity(city);
        address.setStateProvince(state);
        address.setPostalCode(zipCode);
        address.setCountry(country);
        address.setAddressType(type);
        address.setIsDefault(type == Address.AddressType.SHIPPING);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        return addressRepository.save(address);
    }
}
