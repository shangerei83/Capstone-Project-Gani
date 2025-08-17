package com.capstone.service;

import com.capstone.cart.CartItem;
import com.capstone.cart.CartSummary;
import com.capstone.domain.Order;

import java.util.List;

public interface CheckoutService {
	Order createOrderFromCart(String customerEmail, String shippingFullName, String shippingAddressLine, String city, String state, String postalCode, String country);
}
