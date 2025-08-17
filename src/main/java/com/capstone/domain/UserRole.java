package com.capstone.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * UserRole entity representing the many-to-many relationship between User and Role
 * 
 * This associative entity manages user role assignments with additional
 * metadata like assignment date and who assigned the role.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Entity
@Table(name = "user_roles")
@EntityListeners(AuditingEntityListener.class)
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @CreatedDate
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedBy;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Constructors
    public UserRole() {}

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
        this.isActive = true;
    }

    public UserRole(User user, Role role, User assignedBy) {
        this.user = user;
        this.role = role;
        this.assignedBy = assignedBy;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(User assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // Helper methods
    public String getRoleName() {
        return role != null ? role.getName() : null;
    }

    public String getUserEmail() {
        return user != null ? user.getEmail() : null;
    }

    public String getAssignedByEmail() {
        return assignedBy != null ? assignedBy.getEmail() : null;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", user=" + (user != null ? user.getEmail() : "null") +
                ", role=" + (role != null ? role.getName() : "null") +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRole userRole = (UserRole) o;
        return id != null && id.equals(userRole.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
