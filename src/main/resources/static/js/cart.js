document.addEventListener('DOMContentLoaded', () => {
    const addToCartForms = document.querySelectorAll('form[action="/cart/add"]');

    addToCartForms.forEach(form => {
        form.addEventListener('submit', async (event) => {
            event.preventDefault();

            const formData = new FormData(form);
            const productId = formData.get('productId');
            const quantity = formData.get('quantity');

            try {
                const response = await fetch('/cart/add', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        productId: productId,
                        quantity: quantity
                    })
                });

                if (response.ok) {
                    const result = await response.json();
                    updateCartCount(result.cartItemCount);
                    showNotification('Product added to cart successfully!', 'success');
                } else {
                    const error = await response.text();
                    showNotification(`Error: ${error}`, 'error');
                }
            } catch (error) {
                showNotification('An unexpected error occurred.', 'error');
            }
        });
    });

    function updateCartCount(count) {
        const cartCountElement = document.getElementById('cart-item-count');
        if (cartCountElement) {
            cartCountElement.textContent = count;
        }
    }

    function showNotification(message, type) {
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.textContent = message;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.remove();
        }, 3000);
    }
});