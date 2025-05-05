function attachTableSearch(tableId, options = {}) {
    // Find all search inputs for the specified table by matching data-table-id
    const searchInputs = document.querySelectorAll(`.search-input[data-table-id="${tableId}"]`);
    const table = document.getElementById(tableId);
    if (!table) return;  // Ensure the table exists

    const rows = Array.from(table.querySelector("tbody").rows);

    // Default options
    const settings = {
        columnsToSearch: [], // Empty array means search all columns
        excludeRowsClass: 'exclude-from-search',
        highlight: true,
        ...options,
    };

    // Function to get match score
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
    function filterAndReorderRows(searchTerm) {
        if (searchTerm === '') {
            rows.forEach(row => {
                if (!row.classList.contains(settings.excludeRowsClass)) {
                    row.style.display = '';
                    // Reset all cell highlights
                    Array.from(row.cells).forEach(cell => {
                        cell.innerHTML = cell.textContent;
                    });
                }
            });
            return;
        }

        // First, reset all highlights
        rows.forEach(row => {
            if (!row.classList.contains(settings.excludeRowsClass)) {
                Array.from(row.cells).forEach(cell => {
                    cell.innerHTML = cell.textContent; // Clear old highlights
                });
            }
        });

        // Sort rows based on the match score (across all cells)
        rows.sort((a, b) => {
            const aScore = Array.from(a.cells).reduce((score, cell, index) => {
                if (settings.columnsToSearch.length === 0 || settings.columnsToSearch.includes(index)) {
                    return score + getMatchScore(cell.textContent.toLowerCase(), searchTerm);
                }
                return score;
            }, 0);

            const bScore = Array.from(b.cells).reduce((score, cell, index) => {
                if (settings.columnsToSearch.length === 0 || settings.columnsToSearch.includes(index)) {
                    return score + getMatchScore(cell.textContent.toLowerCase(), searchTerm);
                }
                return score;
            }, 0);

            return bScore - aScore; // Sort by descending match score
        });

        // Reorder rows and add them back to the table body
        const tbody = table.querySelector("tbody");
        tbody.innerHTML = '';
        rows.forEach(row => tbody.appendChild(row));

        // Now filter rows based on search term
        rows.forEach(row => {
            if (row.classList.contains(settings.excludeRowsClass)) return; // Exclude specific rows

            let rowContainsMatch = false;

            // Check if any cell in the row matches the search term
            Array.from(row.cells).forEach((cell, index) => {
                if (settings.columnsToSearch.length === 0 || settings.columnsToSearch.includes(index)) {
                    if (cell.textContent.toLowerCase().includes(searchTerm)) {
                        rowContainsMatch = true;
                        if (settings.highlight) {
                            highlightMatches(cell, searchTerm); // Highlight matching cells
                        }
                    }
                }
            });

            if (rowContainsMatch) {
                row.style.display = ''; // Show row if it contains a match
            } else {
                row.style.display = 'none'; // Hide row if no match
            }
        });
    }

    // Attach event listener to each search input for the specific table
    searchInputs.forEach(input => {
        input.addEventListener("input", () => {
            const searchTerm = input.value.toLowerCase();
            filterAndReorderRows(searchTerm);
        });
    });
}
