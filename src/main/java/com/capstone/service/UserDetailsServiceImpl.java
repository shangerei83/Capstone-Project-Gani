package com.capstone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capstone.domain.User;
import com.capstone.repository.UserRepository;

import java.util.stream.Collectors;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("=== UserDetailsService.loadUserByUsername called with email: {} ===", email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", email);
                        return new UsernameNotFoundException("User not found with email: " + email);
                    });

            logger.info("Found user: {} (ID: {}, Active: {})", user.getEmail(), user.getId(), user.getIsActive());
            logger.info("User password hash: {}", user.getPasswordHash());
            
            String roles = user.getUserRoles().stream()
                    .map(userRole -> userRole.getRole().getName())
                    .collect(Collectors.joining(", "));
            logger.info("User roles: {}", roles);

            List<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
                    .map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getName()))
                    .collect(Collectors.toList());
            
            logger.info("Created authorities: {}", authorities);

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPasswordHash())
                    .authorities(authorities)
                    .disabled(!user.getIsActive())
                    .build();
                    
        } catch (Exception e) {
            logger.error("Error in loadUserByUsername for email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }
}
