package com.capstone.service;

import java.math.BigDecimal;

/**
 * Order Statistics - Data transfer object for order statistics
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
public class OrderStatistics {
    
    private long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private long pendingOrders;
    private long completedOrders;
    private long cancelledOrders;
    
    public OrderStatistics() {}
    
    public OrderStatistics(long totalOrders, BigDecimal totalRevenue, BigDecimal averageOrderValue,
                          long pendingOrders, long completedOrders, long cancelledOrders) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = averageOrderValue;
        this.pendingOrders = pendingOrders;
        this.completedOrders = completedOrders;
        this.cancelledOrders = cancelledOrders;
    }
    
    // Getters and Setters
    public long getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }
    
    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
    
    public long getPendingOrders() {
        return pendingOrders;
    }
    
    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }
    
    public long getCompletedOrders() {
        return completedOrders;
    }
    
    public void setCompletedOrders(long completedOrders) {
        this.completedOrders = completedOrders;
    }
    
    public long getCancelledOrders() {
        return cancelledOrders;
    }
    
    public void setCancelledOrders(long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }
}
