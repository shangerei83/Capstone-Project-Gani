package com.capstone.service.impl;

import com.capstone.cart.CartItem;
import com.capstone.cart.CartSummary;
import com.capstone.domain.*;
import com.capstone.repository.OrderItemRepository;
import com.capstone.repository.OrderRepository;
import com.capstone.repository.UserRepository;
import com.capstone.repository.ProductRepository;
import com.capstone.service.CartService;
import com.capstone.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CheckoutServiceImpl implements CheckoutService {

	private final CartService cartService;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	@Autowired
	public CheckoutServiceImpl(CartService cartService,
	                          OrderRepository orderRepository,
	                          OrderItemRepository orderItemRepository,
	                          UserRepository userRepository,
	                          ProductRepository productRepository) {
		this.cartService = cartService;
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
	}

	@Override
	@Transactional
	public Order createOrderFromCart(String customerEmail, String shippingFullName, String shippingAddressLine, String city, String state, String postalCode, String country) {
		List<CartItem> items = cartService.getItems();
		if (items == null || items.isEmpty()) {
			throw new IllegalStateException("Cart is empty");
		}

		User user = userRepository.findByEmail(customerEmail).orElseGet(() -> {
			User u = new User();
			u.setEmail(customerEmail);
			// Parse full name into first/last; fallback defaults
			String first = shippingFullName != null ? shippingFullName.trim() : "Customer";
			String last = "Customer";
			if (first.contains(" ")) {
				int idx = first.indexOf(' ');
				last = first.substring(idx + 1).trim();
				first = first.substring(0, idx).trim();
			}
			u.setFirstName(first.isEmpty() ? "Customer" : first);
			u.setLastName(last.isEmpty() ? "Customer" : last);
			// Set placeholder password to satisfy validation (security will override later)
			u.setPasswordHash("changeme123");
			u.setIsActive(true);
			u.setCreatedAt(LocalDateTime.now());
			return userRepository.save(u);
		});

		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
		order.setUser(user);
		order.setOrderStatus(Order.OrderStatus.PENDING);

		CartSummary summary = cartService.getSummary();
		order.setSubtotal(summary.getSubtotal());
		order.setTaxAmount(summary.getTax());
		order.setShippingAmount(summary.getShipping());
		order.setTotalAmount(summary.getTotal());

		order = orderRepository.save(order);

		for (CartItem ci : items) {
			OrderItem oi = new OrderItem(ci.getQuantity(), ci.getUnitPrice(), null);
			productRepository.findById(ci.getId()).ifPresent(oi::setProduct);
			oi.setOrder(order);
			orderItemRepository.save(oi);
		}

		cartService.clear();
		return order;
	}
}
