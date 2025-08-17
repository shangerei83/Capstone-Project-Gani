package com.capstone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.capstone.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(authz -> authz
                // Публичные страницы
                .requestMatchers("/", "/home", "/catalog", "/about", "/contact").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                
                // Защищенные страницы - требуют аутентификации
                .requestMatchers("/cart/**", "/checkout/**", "/profile/**").authenticated()
                
                // Страницы продавца - требуют роли SELLER
                .requestMatchers("/seller/**").hasRole("SELLER")
                
                // Админские страницы - требуют роли ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Все остальные запросы требуют аутентификации
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .defaultSuccessUrl("/home")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/home")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions().sameOrigin() // Для H2 Console
            );

        return http.build();
    }
}
