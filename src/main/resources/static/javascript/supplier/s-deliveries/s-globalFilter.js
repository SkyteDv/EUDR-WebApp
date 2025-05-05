// Global state for filter results
let notAttachedFilterState = null;
let attachedFilterState = null;

// Function to apply the filter
function applyFilters() {
    // Get all filter values
    const filterScope = document.querySelector('.filter-choice-input').value;  // Global or by status
    const customerFilter = document.querySelector('.filter-text-input').value.toLowerCase();
    const fromDate = document.querySelector('#from').value;
    const tillDate = document.querySelector('#till').value;
    const ddsAvailable = document.querySelector('#ddsAvailable').checked;

    console.log("Filter Scope:", filterScope);
    console.log("Customer Filter:", customerFilter);
    console.log("From Date:", fromDate);
    console.log("Till Date:", tillDate);
    console.log("DDS Available:", ddsAvailable);

    // Get all table rows for active deliveries
    const activeRows = document.querySelectorAll('#activeDeliveriesTable tbody tr');
    console.log("Active Rows:", activeRows);

    // Get all attached delivery cards for done deliveries
    const doneRows = document.querySelectorAll('.attached-cards-container .attached-card');
    console.log("Done Rows:", doneRows);

    // Function to apply filter on a row/card
    function checkRow(row, type) {
        let match = true;

        // Get the row's data based on whether it's a table row or a card
        let customer = '';
        let orderDate = '';
        let ddsRef = '';

        if (type === 'active') {
            // Active row (table)
            customer = row.querySelector('td:nth-child(3)') ? row.querySelector('td:nth-child(3)').innerText.toLowerCase() : '';
            orderDate = row.querySelector('td:nth-child(4)') ? row.querySelector('td:nth-child(4)').innerText : '';
            ddsRef = row.querySelector('td:nth-child(2)') ? row.querySelector('td:nth-child(2)').innerText : '';
        } else if (type === 'done') {
            // Done row (attached card)
            customer = row.querySelector('.right-card-internal-data p:nth-child(3) span') ?
                row.querySelector('.right-card-internal-data p:nth-child(3) span').innerText.toLowerCase() : '';
            orderDate = row.querySelector('.right-card-internal-data p:nth-child(4) span') ?
                row.querySelector('.right-card-internal-data p:nth-child(4) span').innerText : '';
            ddsRef = row.querySelector('.right-card-internal-data p:nth-child(2) span') ?
                row.querySelector('.right-card-internal-data p:nth-child(2) span').innerText : '';
        }

        console.log(`Checking row with customer: ${customer}, orderDate: ${orderDate}, ddsRef: ${ddsRef}`);

        // Filter for customer
        if (customerFilter && !customer.includes(customerFilter)) {
            match = false;
            console.log("Customer doesn't match filter.");
        }

        // Filter for date range
        if (fromDate && orderDate < fromDate) {
            match = false;
            console.log("Order date is before 'from' date.");
        }
        if (tillDate && orderDate > tillDate) {
            match = false;
            console.log("Order date is after 'till' date.");
        }

        // Filter for DDS availability (attached or not)
        if (ddsAvailable && ddsRef === 'Denied') {
            match = false;
            console.log("DDS is denied but DDS Available is checked.");
        }

        console.log("Row matches:", match);
        return match;
    }

    // Apply filters to active deliveries (table)
    if (filterScope === 'not-attached' || filterScope === 'global') {
        activeRows.forEach(row => {
            const matches = checkRow(row, 'active');
            // Store the state for 'Not attached'
            notAttachedFilterState = matches;
            row.style.display = matches ? '' : 'none';  // Show/hide based on filter
        });
    }

    // Apply filters to done deliveries (attached cards)
    if (filterScope === 'attached' || filterScope === 'global') {
        doneRows.forEach(card => {
            const matches = checkRow(card, 'done');
            // Store the state for 'Attached'
            attachedFilterState = matches;
            card.style.display = matches ? '' : 'none';  // Show/hide based on filter
        });
    }
}

// Function to reset the filter
function resetFilters() {
    // Reset all filter fields
    document.querySelector('.filter-choice-input').value = 'global';  // Default to global
    document.querySelector('.filter-text-input').value = '';
    document.querySelector('#from').value = '';
    document.querySelector('#till').value = '';
    document.querySelector('#ddsAvailable').checked = false;

    console.log("Filters reset.");

    // Show all rows and cards again
    const activeRows = document.querySelectorAll('#activeDeliveriesTable tbody tr');
    const doneRows = document.querySelectorAll('.attached-cards-container .attached-card');

    activeRows.forEach(row => {
        row.style.display = '';  // Show all active deliveries
    });
    doneRows.forEach(card => {
        card.style.display = '';  // Show all done deliveries
    });

    console.log("All rows and cards are visible again.");
}

// Event listeners for Apply and Reset buttons
document.querySelector('#applyFilterButton').addEventListener('click', applyFilters);
document.querySelector('#resetFilterButton').addEventListener('click', resetFilters);
