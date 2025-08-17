package com.capstone.service;

import com.capstone.cart.CartItem;
import com.capstone.cart.CartSummary;

import java.util.List;

public interface CartService {
	List<CartItem> getItems();
	CartSummary getSummary();
	void addItem(Long productId, int quantity);
	void updateQuantity(Long productId, int quantity);
	void removeItem(Long productId);
	void clear();
}
