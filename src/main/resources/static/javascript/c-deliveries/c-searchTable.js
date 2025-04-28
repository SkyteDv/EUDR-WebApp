document.addEventListener("DOMContentLoaded", function() {
    const searchInput = document.getElementById("searchInput");
    const table = document.getElementById("ordersTable");
    const rows = Array.from(table.querySelector("tbody").rows);

    // Function to get match score (how well a string matches search term)
    function getMatchScore(str, searchTerm) {
        const regex = new RegExp(searchTerm, 'i'); // Case-insensitive search
        return (str.match(regex) || []).length;
    }

    // Function to highlight search matches in a cell
    function highlightMatches(cell, searchTerm) {
        const text = cell.textContent;
        const regex = new RegExp(`(${searchTerm})`, 'gi');
        const newText = text.replace(regex, '<span class="highlight">$1</span>');
        cell.innerHTML = newText;
    }

    // Function to filter and reorder rows
    function filterAndReorderRows() {
        const searchTerm = searchInput.value.toLowerCase();

        if (searchTerm === '') {
            rows.forEach(row => {
                row.style.display = '';
                // Reset all cell highlights
                Array.from(row.cells).forEach(cell => {
                    cell.innerHTML = cell.textContent;
                });
            });
            return;
        }

        // First, reset all highlights
        rows.forEach(row => {
            Array.from(row.cells).forEach(cell => {
                cell.innerHTML = cell.textContent; // Clear old highlights
            });
        });

        // Sort rows based on the match score (across all cells)
        rows.sort((a, b) => {
            const aScore = Array.from(a.cells).reduce((score, cell) => {
                return score + getMatchScore(cell.textContent.toLowerCase(), searchTerm);
            }, 0);

            const bScore = Array.from(b.cells).reduce((score, cell) => {
                return score + getMatchScore(cell.textContent.toLowerCase(), searchTerm);
            }, 0);

            return bScore - aScore; // Sort by descending match score
        });

        // Reorder rows and add them back to the table body
        const tbody = table.querySelector("tbody");
        tbody.innerHTML = '';
        rows.forEach(row => tbody.appendChild(row));

        // Now filter rows based on search term
        rows.forEach(row => {
            let rowContainsMatch = false;

            // Check if any cell in the row matches the search term
            Array.from(row.cells).forEach(cell => {
                if (cell.textContent.toLowerCase().includes(searchTerm)) {
                    rowContainsMatch = true;
                    highlightMatches(cell, searchInput.value); // Highlight matching cells
                }
            });

            if (rowContainsMatch) {
                row.style.display = ''; // Show row if it contains a match
            } else {
                row.style.display = 'none'; // Hide row if no match
            }
        });
    }

    // Listen for input changes in the search bar
    searchInput.addEventListener("input", filterAndReorderRows);
});
