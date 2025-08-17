package com.capstone.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Shipping entity representing order shipping information
 * 
 * This entity manages shipping details, tracking, and delivery
 * information for orders in the online store.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Entity
@Table(name = "shipping")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_id")
    private Long id;

    @NotNull(message = "Shipping method is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_method", nullable = false)
    private ShippingMethod shippingMethod;

    @NotNull(message = "Shipping cost is required")
    @PositiveOrZero(message = "Shipping cost cannot be negative")
    @Column(name = "shipping_cost", precision = 10, scale = 2, nullable = false)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "carrier_name")
    private String carrierName;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "shipping_status")
    @Enumerated(EnumType.STRING)
    private ShippingStatus shippingStatus = ShippingStatus.PENDING;

    @Column(name = "shipping_notes")
    private String shippingNotes;

    @Column(name = "package_weight")
    private BigDecimal packageWeight;

    @Column(name = "package_dimensions")
    private String packageDimensions;

    @Column(name = "signature_required")
    private Boolean signatureRequired = false;

    @Column(name = "insurance_amount", precision = 10, scale = 2)
    private BigDecimal insuranceAmount = BigDecimal.ZERO;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    // Enums
    public enum ShippingMethod {
        STANDARD, EXPRESS, OVERNIGHT, SAME_DAY, PICKUP
    }

    public enum ShippingStatus {
        PENDING, PROCESSING, SHIPPED, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, FAILED, RETURNED
    }

    // Custom constructor for shipping creation
    public Shipping(ShippingMethod shippingMethod, BigDecimal shippingCost, Order order) {
        this.shippingMethod = shippingMethod;
        this.shippingCost = shippingCost;
        this.order = order;
        this.shippingStatus = ShippingStatus.PENDING;
    }

    // Helper methods
    public void markAsShipped(String trackingNumber, String carrierName) {
        this.shippingStatus = ShippingStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
        this.carrierName = carrierName;
        this.shippedAt = LocalDateTime.now();
    }

    public void markAsInTransit() {
        this.shippingStatus = ShippingStatus.IN_TRANSIT;
    }

    public void markAsOutForDelivery() {
        this.shippingStatus = ShippingStatus.OUT_FOR_DELIVERY;
    }

    public void markAsDelivered() {
        this.shippingStatus = ShippingStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.shippingStatus = ShippingStatus.FAILED;
    }

    public void markAsReturned() {
        this.shippingStatus = ShippingStatus.RETURNED;
    }

    public boolean isShipped() {
        return ShippingStatus.SHIPPED.equals(shippingStatus) ||
               ShippingStatus.IN_TRANSIT.equals(shippingStatus) ||
               ShippingStatus.OUT_FOR_DELIVERY.equals(shippingStatus);
    }

    public boolean isDelivered() {
        return ShippingStatus.DELIVERED.equals(shippingStatus);
    }

    public boolean isFailed() {
        return ShippingStatus.FAILED.equals(shippingStatus);
    }

    public boolean isReturned() {
        return ShippingStatus.RETURNED.equals(shippingStatus);
    }

    public boolean hasTracking() {
        return trackingNumber != null && !trackingNumber.trim().isEmpty();
    }

    public String getShippingMethodDisplayName() {
        switch (shippingMethod) {
            case STANDARD:
                return "Standard Shipping";
            case EXPRESS:
                return "Express Shipping";
            case OVERNIGHT:
                return "Overnight Shipping";
            case SAME_DAY:
                return "Same Day Delivery";
            case PICKUP:
                return "Store Pickup";
            default:
                return shippingMethod.toString();
        }
    }

    public String getEstimatedDeliveryDisplay() {
        if (estimatedDeliveryDate != null) {
            return estimatedDeliveryDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        }
        return "TBD";
    }

    public boolean isDelayed() {
        if (estimatedDeliveryDate != null && deliveredAt == null) {
            return LocalDateTime.now().isAfter(estimatedDeliveryDate);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Shipping{" +
                "id=" + id +
                ", shippingMethod=" + shippingMethod +
                ", shippingCost=" + shippingCost +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", shippingStatus=" + shippingStatus +
                ", order=" + (order != null ? order.getOrderNumber() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shipping shipping = (Shipping) o;
        return id != null && id.equals(shipping.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
