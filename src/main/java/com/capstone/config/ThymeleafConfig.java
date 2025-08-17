package com.capstone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

/**
 * Thymeleaf Configuration for GaniMart
 * 
 * This configuration provides:
 * - Static resource handling
 * - Template engine configuration
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Configuration
public class ThymeleafConfig implements WebMvcConfigurer {

    /**
     * Configure static resource handlers
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/css/**")
            .addResourceLocations("classpath:/static/css/");
        
        registry
            .addResourceHandler("/js/**")
            .addResourceLocations("classpath:/static/css/");
        
        registry
            .addResourceHandler("/images/**")
            .addResourceLocations("classpath:/static/images/");
        
        registry
            .addResourceHandler("/assets/**")
            .addResourceLocations("classpath:/static/assets/");
    }

    /**
     * Enable Spring Security dialect for Thymeleaf
     */
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
}
