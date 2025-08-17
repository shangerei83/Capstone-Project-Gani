package com.capstone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot Application class for GaniMart - Online Store Marketplace
 * 
 * This application implements a three-tier architecture as required by Stage 5:
 * 
 * 1. PRESENTATION TIER (Controllers + Thymeleaf Views)
 *    - Controllers handle HTTP requests and responses
 *    - Thymeleaf templates render the user interface
 *    - Minimum 6 views required
 * 
 * 2. APPLICATION TIER (Services)
 *    - Business logic and data processing
 *    - Services use dependencies from persistence layer
 *    - Services do not interact with database directly
 * 
 * 3. DATA TIER (Persistence Layer)
 *    - JPA entities and repositories
 *    - Database access through Spring Data JPA
 *    - Minimum 4 entities required with relationships
 * 
 * @author Capstone Student
 * @version 1.0.0
 * @since 2024
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.capstone.repository")
@EnableCaching
public class GaniMartApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaniMartApplication.class, args);
        
        System.out.println("🚀 ========================================");
        System.out.println("🚀 GaniMart Application Started Successfully!");
        System.out.println("🚀 ========================================");
        System.out.println("📍 Application URL: http://localhost:8080/store");
        System.out.println("🔧 H2 Console: http://localhost:8080/store/h2-console");
        System.out.println("📊 Database: H2 (in-memory)");
        System.out.println("🎨 Template Engine: Thymeleaf");
        System.out.println("🔐 Security: Spring Security");
        System.out.println("📚 JPA: Spring Data JPA + Hibernate");
        System.out.println("🧪 Testing: JUnit + Mockito + JaCoCo");
        System.out.println("🚀 ========================================");
    }
}
