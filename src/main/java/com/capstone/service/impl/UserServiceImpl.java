package com.capstone.service.impl;

import com.capstone.domain.User;
import com.capstone.domain.Role;
import com.capstone.domain.UserRole;
import com.capstone.repository.UserRepository;
import com.capstone.repository.RoleRepository;
import com.capstone.repository.UserRoleRepository;
import com.capstone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Service Implementation - Application Layer
 * 
 * This service implements business logic for user management and demonstrates
 * the separation of concerns in the three-tier architecture.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, 
                         RoleRepository roleRepository,
                         UserRoleRepository userRoleRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }



    @Override
    public User createUser(User user) {
        // Check if user already exists
        if (userExistsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        // Encode password and set it
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        
        // Set default role (CUSTOMER) if no roles specified
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Default role ROLE_CUSTOMER not found"));
            
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(customerRole);
            user.getUserRoles().add(userRole);
        }

        // Set default values
        user.setIsActive(true);
        
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update fields
        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setPhone(userDetails.getPhone());
        existingUser.setIsActive(userDetails.getIsActive());

        // Update password only if provided
        if (userDetails.getPasswordHash() != null && !userDetails.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userDetails.getPasswordHash()));
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Soft delete - just deactivate
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }



    @Override
    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByUserRolesRoleName(roleName);
    }

    @Override
    public User activateUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setIsActive(true);
        return userRepository.save(user);
    }

    @Override
    public User deactivateUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setIsActive(false);
        return userRepository.save(user);
    }
}
