package com.capstone.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Role entity representing user roles in the system
 * 
 * This entity defines different roles that users can have
 * (e.g., CUSTOMER, SELLER, ADMIN) and their permissions.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Entity
@Table(name = "roles")
@EntityListeners(AuditingEntityListener.class)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name cannot exceed 50 characters")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @Size(max = 1000, message = "Permissions cannot exceed 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String permissions;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_system_role")
    private Boolean isSystemRole = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-Many relationship with User through UserRole
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new HashSet<>();

    // Constructors
    public Role() {}

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
        this.isActive = true;
    }

    public Role(String name, String description, String permissions) {
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsSystemRole() {
        return isSystemRole;
    }

    public void setIsSystemRole(Boolean isSystemRole) {
        this.isSystemRole = isSystemRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    // Helper methods
    public boolean isCustomerRole() {
        return "ROLE_CUSTOMER".equals(name);
    }

    public boolean isSellerRole() {
        return "ROLE_SELLER".equals(name);
    }

    public boolean isAdminRole() {
        return "ROLE_ADMIN".equals(name);
    }

    public void addUserRole(UserRole userRole) {
        userRoles.add(userRole);
        userRole.setRole(this);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id != null && id.equals(role.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
