package com.capstone.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
 * Review entity representing product reviews by users
 * 
 * This entity manages user feedback, ratings, and comments
 * for products in the online store.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Entity
@Table(name = "reviews")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @NotBlank(message = "Review title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    @Column(name = "review_title", nullable = false)
    private String reviewTitle;

    @Size(max = 1000, message = "Review content cannot exceed 1000 characters")
    @Column(name = "review_content", columnDefinition = "TEXT")
    private String reviewContent;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(nullable = false)
    private Integer rating;

    @Column(name = "is_verified_purchase")
    private Boolean isVerifiedPurchase = false;

    @Column(name = "is_helpful_count")
    private Integer helpfulCount = 0;

    @Column(name = "is_reported")
    private Boolean isReported = false;

    @Column(name = "is_approved")
    private Boolean isApproved = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;



    // Helper methods
    public BigDecimal getRatingAsBigDecimal() {
        return new BigDecimal(rating);
    }

    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    public void incrementHelpfulCount() {
        this.helpfulCount = (this.helpfulCount == null ? 0 : this.helpfulCount) + 1;
    }

    public boolean isPositive() {
        return rating >= 4;
    }

    public boolean isNegative() {
        return rating <= 2;
    }

    public boolean isNeutral() {
        return rating == 3;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", reviewTitle='" + reviewTitle + '\'' +
                ", rating=" + rating +
                ", user=" + (user != null ? user.getEmail() : "null") +
                ", product=" + (product != null ? product.getTitle() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id != null && id.equals(review.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
