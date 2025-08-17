package com.capstone.repository;

import com.capstone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository - Data Access Layer
 * 
 * This repository handles database operations for User entities.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     * 
     * @param email user email
     * @return optional user
     */
    Optional<User> findByEmail(String email);

    /**
     * Find active users only
     * 
     * @return list of active users
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findActiveUsers();

    /**
     * Find users by role
     * 
     * @param roleName role name
     * @return list of users with specified role
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.name = :roleName AND u.isActive = true")
    List<User> findByRoleName(@Param("roleName") String roleName);

    /**
     * Find users by first name or last name containing
     * 
     * @param name name to search for
     * @return list of matching users
     */
    @Query("SELECT u FROM User u WHERE (u.firstName LIKE %:name% OR u.lastName LIKE %:name%) AND u.isActive = true")
    List<User> findByNameContaining(@Param("name") String name);

    /**
     * Check if email exists
     * 
     * @param email email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find user by email (alias for findByEmail for backward compatibility)
     * 
     * @param email email
     * @return optional user
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByUsername(@Param("email") String email);
    
    /**
     * Check if email exists (alias for existsByEmail for backward compatibility)
     * 
     * @param email email to check
     * @return true if email exists
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByUsername(@Param("email") String email);
    
    /**
     * Find users by role name using Spring Data JPA
     * 
     * @param roleName role name
     * @return list of users with specified role
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.name = :roleName")
    List<User> findByUserRolesRoleName(@Param("roleName") String roleName);
    
    /**
     * Find users by active status
     * 
     * @param isActive active status
     * @return list of users with specified active status
     */
    List<User> findByIsActive(boolean isActive);
    
    /**
     * Find user by email and active status
     * 
     * @param email email
     * @param isActive active status
     * @return optional user
     */
    Optional<User> findByEmailAndIsActive(String email, boolean isActive);
    
    /**
     * Find user by username (email) and active status
     * 
     * @param email email
     * @param isActive active status
     * @return optional user
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = :isActive")
    Optional<User> findByUsernameAndIsActive(@Param("email") String email, @Param("isActive") boolean isActive);
}
