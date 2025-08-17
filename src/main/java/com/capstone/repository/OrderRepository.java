package com.capstone.repository;

import com.capstone.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<Order> findByOrderNumber(String orderNumber);

	@Query("SELECT COUNT(o) FROM Order o")
	long countAll();

	/**
	 * Find orders by user ID
	 *
	 * @param userId user ID
	 * @return list of orders for the specified user
	 */
	List<Order> findByUserId(Long userId);

	/**
	 * Find orders by status
	 *
	 * @param status order status
	 * @return list of orders with specified status
	 */
	List<Order> findByOrderStatus(Order.OrderStatus status);

	/**
	 * Find orders by user ID and status
	 *
	 * @param userId user ID
	 * @param status order status
	 * @return list of orders for the specified user and status
	 */
	List<Order> findByUserIdAndOrderStatus(Long userId, Order.OrderStatus status);
}