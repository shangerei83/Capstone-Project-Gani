package com.capstone.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment entity representing order payments
 * 
 * This entity manages payment information, methods, and status
 * for orders in the online store.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Entity
@Table(name = "payments")
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "payment_reference", unique = true, nullable = false)
    private String paymentReference;

    @NotNull(message = "Payment amount is required")
    @Positive(message = "Payment amount must be positive")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "gateway_response")
    private String gatewayResponse;

    @Column(name = "gateway_error_code")
    private String gatewayErrorCode;

    @Column(name = "gateway_error_message")
    private String gatewayErrorMessage;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(name = "refund_reason")
    private String refundReason;

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
    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER, CASH_ON_DELIVERY, CRYPTO
    }

    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED
    }

    // Constructors
    public Payment() {}

    public Payment(String paymentReference, BigDecimal amount, PaymentMethod paymentMethod, Order order) {
        this.paymentReference = paymentReference;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.order = order;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getGatewayResponse() {
        return gatewayResponse;
    }

    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }

    public String getGatewayErrorCode() {
        return gatewayErrorCode;
    }

    public void setGatewayErrorCode(String gatewayErrorCode) {
        this.gatewayErrorCode = gatewayErrorCode;
    }

    public String getGatewayErrorMessage() {
        return gatewayErrorMessage;
    }

    public void setGatewayErrorMessage(String gatewayErrorMessage) {
        this.gatewayErrorMessage = gatewayErrorMessage;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    // Helper methods
    public void markAsCompleted(String transactionId) {
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorCode, String errorMessage) {
        this.paymentStatus = PaymentStatus.FAILED;
        this.gatewayErrorCode = errorCode;
        this.gatewayErrorMessage = errorMessage;
        this.failedAt = LocalDateTime.now();
    }

    public void markAsRefunded(BigDecimal refundAmount, String reason) {
        this.paymentStatus = PaymentStatus.REFUNDED;
        this.refundAmount = refundAmount;
        this.refundReason = reason;
        this.refundedAt = LocalDateTime.now();
    }

    public void markAsPartiallyRefunded(BigDecimal refundAmount, String reason) {
        this.paymentStatus = PaymentStatus.PARTIALLY_REFUNDED;
        this.refundAmount = refundAmount;
        this.refundReason = reason;
        this.refundedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return PaymentStatus.COMPLETED.equals(paymentStatus);
    }

    public boolean isFailed() {
        return PaymentStatus.FAILED.equals(paymentStatus);
    }

    public boolean isRefunded() {
        return PaymentStatus.REFUNDED.equals(paymentStatus) || 
               PaymentStatus.PARTIALLY_REFUNDED.equals(paymentStatus);
    }

    public boolean canBeRefunded() {
        return isCompleted() && !isRefunded();
    }

    public BigDecimal getNetAmount() {
        return amount.subtract(refundAmount);
    }

    public String getPaymentMethodDisplayName() {
        switch (paymentMethod) {
            case CREDIT_CARD:
                return "Credit Card";
            case DEBIT_CARD:
                return "Debit Card";
            case PAYPAL:
                return "PayPal";
            case BANK_TRANSFER:
                return "Bank Transfer";
            case CASH_ON_DELIVERY:
                return "Cash on Delivery";
            case CRYPTO:
                return "Cryptocurrency";
            default:
                return paymentMethod.toString();
        }
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", paymentReference='" + paymentReference + '\'' +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", paymentStatus=" + paymentStatus +
                ", order=" + (order != null ? order.getOrderNumber() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return id != null && id.equals(payment.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
