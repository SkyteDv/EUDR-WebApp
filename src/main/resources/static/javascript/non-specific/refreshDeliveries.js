import { showLoadingOverlay, hideLoadingOverlay } from '/javascript/non-specific/loading-spinner.js';

function showWarning(secondsLeft) {
    // Remove previous warning if any
    const existingWarning = document.getElementById('warningMessage');
    if (existingWarning) {
        existingWarning.remove();
    }

    // Inject styles if not already present
    if (!document.getElementById('warningMessageStyles')) {
        const style = document.createElement('style');
        style.id = 'warningMessageStyles';
        style.textContent = `
          #warningMessage {
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
            padding: 15px 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(255, 0, 0, 0.1);
            z-index: 1000000000;
            display: flex;
            align-items: center;
            max-width: 320px;
            font-family: Arial, sans-serif;
            font-size: 14px;
          }
          #warningMessage button {
            margin-left: 15px;
            background: none;
            border: none;
            font-weight: bold;
            color: #721c24;
            cursor: pointer;
            font-size: 16px;
            line-height: 1;
          }
        `;
        document.head.appendChild(style);
    }

    // Create warning container
    const warningDiv = document.createElement('div');
    warningDiv.id = 'warningMessage';
    warningDiv.textContent = `Please wait another ${secondsLeft} seconds before performing this action`;

    // Create close button
    const closeBtn = document.createElement('button');
    closeBtn.textContent = '×'; // Close icon
    closeBtn.title = 'Close';
    closeBtn.onclick = () => {
        clearTimeout(autoHideTimeout);
        warningDiv.remove();
    };

    warningDiv.appendChild(closeBtn);
    document.body.appendChild(warningDiv);

    // Auto hide after 3 seconds
    const autoHideTimeout = setTimeout(() => {
        warningDiv.remove();
    }, 3000);
}


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

function checkImportStatus() {
    fetch('/api/import-status')
        .then(response => response.json())
        .then(isDone => {
            if (isDone) {
                hideLoadingOverlay();
                location.reload();
            } else {
                // Not done yet, poll again after 1.5 seconds
                setTimeout(checkImportStatus, 1500);
            }
        })
        .catch(error => {
            console.error('Error checking import status:', error);
            hideLoadingOverlay();  // Hide on error to avoid endless loader
        });
}

function refreshDeliveries() {
    const lastRefresh = sessionStorage.getItem('lastRefresh');
    const now = Date.now();
    const diff = now - parseInt(lastRefresh);

    if (diff < 10000) {
        // Less than 10 seconds since last refresh
        console.log('Please wait before refreshing again.');
        showWarning(Math.ceil((10000 - diff) / 1000))
        return;  // skip calling the API
    }

    // Update lastRefresh time
    sessionStorage.setItem('lastRefresh', now.toString());

    showLoadingOverlay('Fetching latest deliveries...');

    fetch('/api/refresh-deliveries', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({}),
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);

            if (data.message === "Deliveries updated successfully") {
                checkImportStatus();
            } else {
                hideLoadingOverlay();
            }
        })
        .catch(error => {
            console.error('Error:', error);
            hideLoadingOverlay();
        });
}




