package com.capstone.repository;

import com.capstone.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserRole Repository - Data Access Layer
 * 
 * This repository handles database operations for UserRole entities.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    /**
     * Find user roles by user ID
     * 
     * @param userId user ID
     * @return list of user roles
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId AND ur.isActive = true")
    List<UserRole> findByUserId(@Param("userId") Long userId);

    /**
     * Find user roles by role ID
     * 
     * @param roleId role ID
     * @return list of user roles
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.role.id = :roleId AND ur.isActive = true")
    List<UserRole> findByRoleId(@Param("roleId") Long roleId);

    /**
     * Find active user roles
     * 
     * @return list of active user roles
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.isActive = true")
    List<UserRole> findActiveUserRoles();

    /**
     * Check if user has specific role
     * 
     * @param userId user ID
     * @param roleId role ID
     * @return true if user has the role
     */
    @Query("SELECT COUNT(ur) > 0 FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.id = :roleId AND ur.isActive = true")
    boolean userHasRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
