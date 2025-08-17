package com.capstone.controller;

import com.capstone.cart.CartItem;
import com.capstone.cart.CartSummary;
import com.capstone.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class CartController {

	private final CartService cartService;

	@Autowired
	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@GetMapping("/cart")
	public String viewCart(Model model) {
		List<CartItem> items = cartService.getItems();
		CartSummary summary = cartService.getSummary();
		model.addAttribute("cartItems", items);
		model.addAttribute("cartSummary", summary);
		model.addAttribute("recentlyViewed", List.of());
		return "cart/cart";
	}

	@PostMapping("/cart/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addToCart(@RequestParam Long productId,
	                        @RequestParam(defaultValue = "1") Integer quantity) {
		cartService.addItem(productId, quantity);
		return ResponseEntity.ok(Map.of(
			"success", true,
			"message", "Product added to cart successfully",
			"cartItemCount", cartService.getItems().size()
		));
	}

	@PostMapping("/cart/update-quantity")
	public String updateQuantity(@RequestParam("itemId") Long productId,
	                             @RequestParam("quantity") Integer quantity) {
		cartService.updateQuantity(productId, quantity != null ? quantity : 1);
		return "redirect:/cart";
	}

	@PostMapping("/cart/remove")
	public String removeItem(@RequestParam("itemId") Long productId) {
		cartService.removeItem(productId);
		return "redirect:/cart";
	}

	@PostMapping("/cart/clear")
	public String clearCart() {
		cartService.clear();
		return "redirect:/cart";
	}
}
