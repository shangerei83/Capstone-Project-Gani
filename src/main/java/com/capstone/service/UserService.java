package com.capstone.service;

import com.capstone.domain.User;
import java.util.List;
import java.util.Optional;

/**
 * User Service - Application Layer
 * 
 * This service handles business logic related to users and demonstrates
 * the separation of concerns in the three-tier architecture.
 * 
 * Responsibilities:
 * - Business logic for user operations
 * - User authentication and authorization
 * - User data processing and validation
 * - NO direct database access (delegates to repository layer)
 * - NO view logic (delegates to controller layer)
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
public interface UserService {

    /**
     * Get all users
     * 
     * @return list of all users
     */
    List<User> getAllUsers();

    /**
     * Get user by ID
     * 
     * @param id user ID
     * @return user or empty if not found
     */
    Optional<User> getUserById(Long id);

    /**
     * Get user by email
     * 
     * @param email user email
     * @return user or empty if not found
     */
    Optional<User> getUserByEmail(String email);



    /**
     * Create new user
     * 
     * @param user user data
     * @return created user
     */
    User createUser(User user);

    /**
     * Update existing user
     * 
     * @param id user ID
     * @param user updated user data
     * @return updated user
     */
    User updateUser(Long id, User user);

    /**
     * Delete user
     * 
     * @param id user ID
     */
    void deleteUser(Long id);

    /**
     * Check if user exists by email
     * 
     * @param email user email
     * @return true if user exists
     */
    boolean userExistsByEmail(String email);



    /**
     * Get users by role
     * 
     * @param roleName role name
     * @return list of users with specified role
     */
    List<User> getUsersByRole(String roleName);

    /**
     * Activate user account
     * 
     * @param id user ID
     * @return updated user
     */
    User activateUser(Long id);

    /**
     * Deactivate user account
     * 
     * @param id user ID
     * @return updated user
     */
    User deactivateUser(Long id);
}
