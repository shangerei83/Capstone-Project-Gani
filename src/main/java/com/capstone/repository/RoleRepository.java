package com.capstone.repository;

import com.capstone.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

/**
 * Role Repository - Data Access Layer
 * 
 * This repository handles database operations for Role entities.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     * 
     * @param name role name
     * @return optional role
     */
    Optional<Role> findByName(String name);

    /**
     * Find active roles only
     * 
     * @return list of active roles
     */
    @Query("SELECT r FROM Role r WHERE r.isActive = true")
    List<Role> findActiveRoles();

    /**
     * Find roles by name containing
     * 
     * @param name name to search for
     * @return list of matching roles
     */
    @Query("SELECT r FROM Role r WHERE r.name LIKE %:name% AND r.isActive = true")
    List<Role> findByNameContaining(@Param("name") String name);
}
