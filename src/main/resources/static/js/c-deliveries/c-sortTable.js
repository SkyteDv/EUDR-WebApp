document.addEventListener("DOMContentLoaded", function() {
    const searchInput = document.getElementById("searchInput");
    const table = document.getElementById("ordersTable");
    const rows = Array.from(table.querySelector("tbody").rows);

    // Function to compare how well a term matches
    function getMatchScore(str, searchTerm) {
        const regex = new RegExp(searchTerm, 'i'); // Case-insensitive search
        return (str.match(regex) || []).length; // Number of matches
    }

    // Function to filter and reorder rows
    function filterAndReorderRows() {
        const searchTerm = searchInput.value.toLowerCase();

        if (searchTerm === '') {
            // If search term is empty, display all rows
            rows.forEach(row => row.style.display = '');
            return;
        }

        // Sort rows by how well they match the search term
        rows.sort((a, b) => {
            const aDdsRef = a.cells[1].textContent.toLowerCase();  // DDS Ref
            const aErpRef = a.cells[2].textContent.toLowerCase();  // Shipment Ref
            const bDdsRef = b.cells[1].textContent.toLowerCase();  // DDS Ref
            const bErpRef = b.cells[2].textContent.toLowerCase();  // Shipment Ref

            // Compute match scores for DDS Ref and Shipment Ref for both rows
            const aScore = getMatchScore(aDdsRef, searchTerm) + getMatchScore(aErpRef, searchTerm);
            const bScore = getMatchScore(bDdsRef, searchTerm) + getMatchScore(bErpRef, searchTerm);

            // Sort in descending order of match score
            return bScore - aScore;
        });

        // Now reorder the rows and make them visible
        const tbody = table.querySelector("tbody");
        tbody.innerHTML = ''; // Clear current rows
        rows.forEach(row => tbody.appendChild(row)); // Re-add sorted rows

        // Filter rows by search term visibility
        rows.forEach(row => {
            const ddsReference = row.cells[1].textContent.toLowerCase();
            const erpReference = row.cells[2].textContent.toLowerCase();

            if (ddsReference.includes(searchTerm) || erpReference.includes(searchTerm)) {
                row.style.display = ''; // Show row if it matches
            } else {
                row.style.display = 'none'; // Hide row if it doesn't match
            }
        });
    }

    // Listen for user input in search bar
    searchInput.addEventListener("input", filterAndReorderRows);
});
