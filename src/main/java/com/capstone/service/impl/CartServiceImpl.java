package com.capstone.service.impl;

import com.capstone.cart.CartItem;
import com.capstone.cart.CartSummary;
import com.capstone.domain.Product;
import com.capstone.repository.ProductRepository;
import com.capstone.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.*;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartServiceImpl implements CartService {

	private static final String SESSION_CART = "CART_ITEMS";

	private final ProductRepository productRepository;
	private final HttpSession httpSession;

	@Autowired
	public CartServiceImpl(ProductRepository productRepository, HttpSession httpSession) {
		this.productRepository = productRepository;
		this.httpSession = httpSession;
	}

	@SuppressWarnings("unchecked")
	private Map<Long, CartItem> getCartMap() {
		Object obj = httpSession.getAttribute(SESSION_CART);
		if (obj instanceof Map) {
			return (Map<Long, CartItem>) obj;
		}
		Map<Long, CartItem> map = new LinkedHashMap<>();
		httpSession.setAttribute(SESSION_CART, map);
		return map;
	}

	@Override
	public List<CartItem> getItems() {
		return new ArrayList<>(getCartMap().values());
	}

	@Override
	public CartSummary getSummary() {
		CartSummary summary = new CartSummary();
		BigDecimal subtotal = getItems().stream()
			.map(CartItem::getSubtotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		summary.setSubtotal(subtotal);
		BigDecimal shipping = subtotal.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(5) : BigDecimal.ZERO;
		summary.setShipping(shipping);
		BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.05));
		summary.setTax(tax);
		summary.setTotal(subtotal.add(shipping).add(tax));
		return summary;
	}

	@Override
	public void addItem(Long productId, int quantity) {
		Map<Long, CartItem> cart = getCartMap();
		CartItem existing = cart.get(productId);
		if (existing != null) {
			existing.setQuantity(existing.getQuantity() + Math.max(1, quantity));
			return;
		}
		Product product = productRepository.findById(productId).orElse(null);
		if (product != null) {
			cart.put(productId, new CartItem(product, quantity));
		}
	}

	@Override
	public void updateQuantity(Long productId, int quantity) {
		Map<Long, CartItem> cart = getCartMap();
		CartItem item = cart.get(productId);
		if (item != null) {
			item.setQuantity(quantity);
		}
	}

	@Override
	public void removeItem(Long productId) {
		getCartMap().remove(productId);
	}

	@Override
	public void clear() {
		getCartMap().clear();
	}
}
