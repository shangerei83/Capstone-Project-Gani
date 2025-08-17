package com.capstone.cart;

import com.capstone.domain.Product;
import java.math.BigDecimal;

public class CartItem {
	private Long id; // productId
	private String title;
	private String imageUrl;
	private String categoryName;
	private BigDecimal unitPrice;
	private int quantity;

	public CartItem(Product product, int quantity) {
		this.id = product.getId();
		this.title = product.getTitle();
		this.imageUrl = product.getImageUrl();
		this.unitPrice = product.getPrice();
		try {
			this.categoryName = (product.getCategory() != null) ? product.getCategory().getName() : null;
		} catch (Exception ignored) {
			this.categoryName = null;
		}
		this.quantity = Math.max(1, quantity);
	}

	public Long getId() { return id; }
	public String getTitle() { return title; }
	public String getImageUrl() { return imageUrl; }
	public String getCategoryName() { return categoryName; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public int getQuantity() { return quantity; }
	public void setQuantity(int quantity) { this.quantity = Math.max(1, quantity); }

	public BigDecimal getSubtotal() {
		return unitPrice.multiply(BigDecimal.valueOf(quantity));
	}
}
