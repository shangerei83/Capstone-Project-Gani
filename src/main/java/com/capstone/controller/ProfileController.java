package com.capstone.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Profile Controller - Presentation Layer
 * 
 * This controller handles user profile requests.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Controller
public class ProfileController {

    /**
     * Display the user profile page
     * 
     * @param model Spring MVC model for template data
     * @return view name for profile page
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        // Set page title
        model.addAttribute("title", "My Profile");
        
        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            model.addAttribute("username", auth.getName());
        }
        
        return "profile/index";
    }
}