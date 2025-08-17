package com.capstone.service.impl;

import com.capstone.domain.Order;
import com.capstone.domain.OrderItem;
import com.capstone.repository.OrderRepository;
import com.capstone.repository.OrderItemRepository;
import com.capstone.service.OrderService;
import com.capstone.service.OrderStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Order Service Implementation - Application Layer
 * 
 * This service implements business logic for orders and demonstrates
 * the separation of concerns in the three-tier architecture.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    @Override
    public Order createOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        
        // Set default values
        if (order.getOrderStatus() == null) {
            order.setOrderStatus(Order.OrderStatus.PENDING);
        }
        
        // Calculate totals
        order = calculateOrderTotals(order);
        
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null");
        }
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        order.setOrderStatus(status);
        
        // Update timestamps based on status
        if (status == Order.OrderStatus.SHIPPED) {
            order.setShippedAt(java.time.LocalDateTime.now());
        } else if (status == Order.OrderStatus.DELIVERED) {
            order.setDeliveredAt(java.time.LocalDateTime.now());
        }
        
        return orderRepository.save(order);
    }

    @Override
    public Order calculateOrderTotals(Order order) {
        if (order == null || order.getOrderItems() == null) {
            return order;
        }
        
        BigDecimal subtotal = BigDecimal.ZERO;
        
        // Calculate subtotal from order items
        for (OrderItem item : order.getOrderItems()) {
            if (item.getPrice() != null && item.getQuantity() != null) {
                BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(itemTotal);
            }
        }
        
        order.setSubtotal(subtotal);
        
        // Calculate tax (assuming 10% tax rate)
        BigDecimal taxRate = new BigDecimal("0.10");
        BigDecimal taxAmount = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        order.setTaxAmount(taxAmount);
        
        // Set shipping amount (assuming $5.00 flat rate)
        BigDecimal shippingAmount = new BigDecimal("5.00");
        order.setShippingAmount(shippingAmount);
        
        // Calculate total
        BigDecimal total = subtotal.add(taxAmount).add(shippingAmount);
        if (order.getDiscountAmount() != null) {
            total = total.subtract(order.getDiscountAmount());
        }
        
        order.setTotalAmount(total);
        
        return order;
    }

    @Override
    public Order addOrderItem(Long orderId, OrderItem orderItem) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (orderItem == null) {
            throw new IllegalArgumentException("Order item cannot be null");
        }
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        orderItem.setOrder(order);
        order.getOrderItems().add(orderItem);
        
        // Recalculate totals
        order = calculateOrderTotals(order);
        
        return orderRepository.save(order);
    }

    @Override
    public Order removeOrderItem(Long orderId, Long itemId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        // Remove item from order
        order.getOrderItems().removeIf(item -> item.getId().equals(itemId));
        
        // Recalculate totals
        order = calculateOrderTotals(order);
        
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderStatistics getOrderStatistics() {
        long totalOrders = orderRepository.countAll();
        
        List<Order> allOrders = orderRepository.findAll();
        
        BigDecimal totalRevenue = allOrders.stream()
            .filter(order -> order.getOrderStatus() == Order.OrderStatus.DELIVERED)
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageOrderValue = totalOrders > 0 ? 
            totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        
        long pendingOrders = allOrders.stream()
            .filter(order -> order.getOrderStatus() == Order.OrderStatus.PENDING)
            .count();
        
        long completedOrders = allOrders.stream()
            .filter(order -> order.getOrderStatus() == Order.OrderStatus.DELIVERED)
            .count();
        
        long cancelledOrders = allOrders.stream()
            .filter(order -> order.getOrderStatus() == Order.OrderStatus.CANCELLED)
            .count();
        
        return new OrderStatistics(totalOrders, totalRevenue, averageOrderValue, 
                                 pendingOrders, completedOrders, cancelledOrders);
    }
}
