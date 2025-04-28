document.addEventListener('DOMContentLoaded', function() {
    // Select the button using its unique ID
    const refreshButton = document.getElementById('refreshDeliveriesButton');

    if (refreshButton) {
        // Add an event listener to the button that calls refreshDeliveries when clicked
        refreshButton.addEventListener('click', refreshDeliveries);
    } else {
        console.error("Button with ID 'refreshDeliveriesButton' not found.");
    }
});

// Define the refreshDeliveries function
function refreshDeliveries() {
    // Make a POST request to refresh the deliveries
    fetch('/api/refresh-deliveries', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            // Add any necessary request body here
        }),
    })
        .then(response => response.json())  // Ensure the response is parsed as JSON
        .then(data => {
            console.log(data); // This should show { message: "Deliveries updated successfully" }

            // Check if the message is exactly what you expect before reloading
            if (data.message === "Deliveries updated successfully") {
                setTimeout(() => {
                    location.reload();
                }, 1500); // Reload the page if the message matches
            }
        })
        .catch(error => {
            console.error('Error:', error); // Log any errors to help debug
        });
}
