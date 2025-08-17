package com.capstone.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    // Custom constructors for role creation
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
