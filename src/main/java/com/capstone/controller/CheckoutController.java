package com.capstone.controller;

import com.capstone.domain.Order;
import com.capstone.service.CartService;
import com.capstone.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutController {

	private final CartService cartService;
	private final CheckoutService checkoutService;

	@Autowired
	public CheckoutController(CartService cartService, CheckoutService checkoutService) {
		this.cartService = cartService;
		this.checkoutService = checkoutService;
	}

	@GetMapping("/checkout")
	public String checkoutForm(Model model) {
		model.addAttribute("cartItems", cartService.getItems());
		model.addAttribute("cartSummary", cartService.getSummary());
		return "checkout/checkout";
	}

	@PostMapping("/checkout")
	public String placeOrder(@RequestParam String email,
	                         @RequestParam String fullName,
	                         @RequestParam String address,
	                         @RequestParam String city,
	                         @RequestParam String state,
	                         @RequestParam String postalCode,
	                         @RequestParam String country,
	                         Model model) {
		Order order = checkoutService.createOrderFromCart(email, fullName, address, city, state, postalCode, country);
		model.addAttribute("order", order);
		return "checkout/success";
	}
}
