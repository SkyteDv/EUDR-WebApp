document.addEventListener('DOMContentLoaded', () => {
    window.markAsShipped = async function (button) {
        const headerDiv = button.closest('.right-card-internal-header');
        const orderId = headerDiv.getAttribute('data-order-id');

        try {
            const response = await fetch('/api/deliveries/update/status', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    orderId: orderId,
                    status: 'shipped'
                })
            });

            const result = await response.json();
            if (response.ok) {
                console.log('Status updated:', result.message);

                const card = headerDiv.closest('.attached-card');
                if (card) {
                    card.style.transition = 'opacity 0.5s ease';
                    card.style.opacity = '0';
                    setTimeout(() => card.remove(), 500);
                }
            } else {
                alert('Failed: ' + result.message);
            }
        } catch (err) {
            console.error('Error updating status:', err);
            alert('Error updating order status.');
        }
    };
});
