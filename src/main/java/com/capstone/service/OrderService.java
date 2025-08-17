package com.capstone.service;

import com.capstone.domain.Order;
import com.capstone.domain.OrderItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Order Service - Application Layer
 * 
 * This service handles business logic related to orders and demonstrates
 * the separation of concerns in the three-tier architecture.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
public interface OrderService {

    /**
     * Get all orders
     * 
     * @return list of all orders
     */
    List<Order> getAllOrders();

    /**
     * Get order by ID
     * 
     * @param id order ID
     * @return order or empty if not found
     */
    Optional<Order> getOrderById(Long id);

    /**
     * Get order by order number
     * 
     * @param orderNumber order number
     * @return order or empty if not found
     */
    Optional<Order> getOrderByOrderNumber(String orderNumber);

    /**
     * Get orders by user ID
     * 
     * @param userId user ID
     * @return list of orders for the specified user
     */
    List<Order> getOrdersByUserId(Long userId);

    /**
     * Get orders by status
     * 
     * @param status order status
     * @return list of orders with specified status
     */
    List<Order> getOrdersByStatus(Order.OrderStatus status);

    /**
     * Create new order
     * 
     * @param order order data
     * @return created order
     */
    Order createOrder(Order order);

    /**
     * Update order status
     * 
     * @param orderId order ID
     * @param status new status
     * @return updated order
     */
    Order updateOrderStatus(Long orderId, Order.OrderStatus status);

    /**
     * Calculate order totals
     * 
     * @param order order to calculate totals for
     * @return order with calculated totals
     */
    Order calculateOrderTotals(Order order);

    /**
     * Add item to order
     * 
     * @param orderId order ID
     * @param orderItem order item to add
     * @return updated order
     */
    Order addOrderItem(Long orderId, OrderItem orderItem);

    /**
     * Remove item from order
     * 
     * @param orderId order ID
     * @param itemId item ID to remove
     * @return updated order
     */
    Order removeOrderItem(Long orderId, Long itemId);

    /**
     * Cancel order
     * 
     * @param orderId order ID
     * @return cancelled order
     */
    Order cancelOrder(Long orderId);

    /**
     * Get order statistics
     * 
     * @return order statistics
     */
    OrderStatistics getOrderStatistics();
}
