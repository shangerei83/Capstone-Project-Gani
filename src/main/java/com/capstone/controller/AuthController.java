package com.capstone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        // Простая проверка (для демонстрации)
        if ("admin@example.com".equals(username) && "password".equals(password)) {
            // Устанавливаем атрибуты сессии
            session.setAttribute("userEmail", username);
            session.setAttribute("userRole", "ADMIN");
            session.setAttribute("isAuthenticated", true);
            
            redirectAttributes.addFlashAttribute("message", "Login successful!");
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials");
            return "redirect:/auth/login?error";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
