package com.capstone.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Inventory entity representing product inventory management
 * 
 * This entity tracks stock levels, reorder points, and inventory
 * movements for products in the online store.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Entity
@Table(name = "inventory")
@EntityListeners(AuditingEntityListener.class)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @NotNull(message = "Current stock is required")
    @PositiveOrZero(message = "Current stock cannot be negative")
    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;

    @Column(name = "minimum_stock")
    private Integer minimumStock = 0;

    @Column(name = "reorder_point")
    private Integer reorderPoint = 10;

    @Column(name = "reorder_quantity")
    private Integer reorderQuantity = 50;

    @Column(name = "max_stock")
    private Integer maxStock;

    @Column(name = "reserved_stock")
    private Integer reservedStock = 0;

    @Column(name = "available_stock")
    private Integer availableStock = 0;

    @Column(name = "last_restocked")
    private LocalDateTime lastRestocked;

    @Column(name = "next_restock_date")
    private LocalDateTime nextRestockDate;

    @Column(name = "is_low_stock")
    private Boolean isLowStock = false;

    @Column(name = "is_out_of_stock")
    private Boolean isOutOfStock = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    // Constructors
    public Inventory() {}

    public Inventory(Product product) {
        this.product = product;
        this.currentStock = 0;
        this.availableStock = 0;
        this.reservedStock = 0;
    }

    public Inventory(Product product, Integer initialStock) {
        this.product = product;
        this.currentStock = initialStock;
        this.availableStock = initialStock;
        this.reservedStock = 0;
        updateStockStatus();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
        updateStockStatus();
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
        updateStockStatus();
    }

    public Integer getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(Integer reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public Integer getReorderQuantity() {
        return reorderQuantity;
    }

    public void setReorderQuantity(Integer reorderQuantity) {
        this.reorderQuantity = reorderQuantity;
    }

    public Integer getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(Integer maxStock) {
        this.maxStock = maxStock;
    }

    public Integer getReservedStock() {
        return reservedStock;
    }

    public void setReservedStock(Integer reservedStock) {
        this.reservedStock = reservedStock;
        updateAvailableStock();
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public LocalDateTime getLastRestocked() {
        return lastRestocked;
    }

    public void setLastRestocked(LocalDateTime lastRestocked) {
        this.lastRestocked = lastRestocked;
    }

    public LocalDateTime getNextRestockDate() {
        return nextRestockDate;
    }

    public void setNextRestockDate(LocalDateTime nextRestockDate) {
        this.nextRestockDate = nextRestockDate;
    }

    public Boolean getIsLowStock() {
        return isLowStock;
    }

    public void setIsLowStock(Boolean isLowStock) {
        this.isLowStock = isLowStock;
    }

    public Boolean getIsOutOfStock() {
        return isOutOfStock;
    }

    public void setIsOutOfStock(Boolean isOutOfStock) {
        this.isOutOfStock = isOutOfStock;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // Helper methods
    public void updateStockStatus() {
        if (currentStock != null) {
            this.isOutOfStock = currentStock <= 0;
            this.isLowStock = currentStock <= reorderPoint;
            updateAvailableStock();
        }
    }

    private void updateAvailableStock() {
        if (currentStock != null && reservedStock != null) {
            this.availableStock = Math.max(0, currentStock - reservedStock);
        }
    }

    public boolean canReserveStock(Integer quantity) {
        return availableStock >= quantity;
    }

    public void reserveStock(Integer quantity) {
        if (canReserveStock(quantity)) {
            this.reservedStock += quantity;
            updateAvailableStock();
        } else {
            throw new IllegalStateException("Insufficient available stock");
        }
    }

    public void releaseReservedStock(Integer quantity) {
        this.reservedStock = Math.max(0, this.reservedStock - quantity);
        updateAvailableStock();
    }

    public void consumeStock(Integer quantity) {
        if (currentStock >= quantity) {
            this.currentStock -= quantity;
            this.reservedStock = Math.max(0, this.reservedStock - quantity);
            updateStockStatus();
        } else {
            throw new IllegalStateException("Insufficient stock");
        }
    }

    public void addStock(Integer quantity) {
        this.currentStock += quantity;
        if (maxStock != null && currentStock > maxStock) {
            this.currentStock = maxStock;
        }
        this.lastRestocked = LocalDateTime.now();
        updateStockStatus();
    }

    public boolean needsRestocking() {
        return currentStock <= reorderPoint;
    }

    public Integer getStockPercentage() {
        if (maxStock == null || maxStock == 0) {
            return 0;
        }
        return (int) ((double) currentStock / maxStock * 100);
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", currentStock=" + currentStock +
                ", availableStock=" + availableStock +
                ", isLowStock=" + isLowStock +
                ", isOutOfStock=" + isOutOfStock +
                ", product=" + (product != null ? product.getTitle() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return id != null && id.equals(inventory.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
