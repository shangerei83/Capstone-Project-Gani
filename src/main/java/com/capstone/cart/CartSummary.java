package com.capstone.cart;

import java.math.BigDecimal;

public class CartSummary {
	private BigDecimal subtotal = BigDecimal.ZERO;
	private BigDecimal shipping = BigDecimal.ZERO;
	private BigDecimal tax = BigDecimal.ZERO;
	private BigDecimal total = BigDecimal.ZERO;

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getShipping() {
		return shipping;
	}

	public void setShipping(BigDecimal shipping) {
		this.shipping = shipping;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}
