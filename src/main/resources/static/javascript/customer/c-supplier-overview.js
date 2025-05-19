document.addEventListener("DOMContentLoaded", () => {
    const table = document.querySelector(".delivery-table");
    const headers = table.querySelectorAll("th.sortable");

    headers.forEach(header => {
        header.addEventListener("click", () => {
            const columnIndex = parseInt(header.getAttribute("data-column"));
            const ascending = !header.classList.contains("sorted-asc");

            sortTable(table, columnIndex, ascending);

            headers.forEach(h => h.classList.remove("sorted-asc", "sorted-desc"));
            header.classList.add(ascending ? "sorted-asc" : "sorted-desc");
        });
    });

    function sortTable(table, columnIndex, ascending) {
        const tbody = table.querySelector("tbody");
        const rows = Array.from(tbody.querySelectorAll("tr"));

        const sortedRows = rows.sort((a, b) => {
            const cellA = a.children[columnIndex]?.textContent.trim() || "";
            const cellB = b.children[columnIndex]?.textContent.trim() || "";

            // Versuche numerische Sortierung
            const numA = parseFloat(cellA.replace('%', '').replace(',', '.'));
            const numB = parseFloat(cellB.replace('%', '').replace(',', '.'));

            if (!isNaN(numA) && !isNaN(numB)) {
                return ascending ? numA - numB : numB - numA;
            }

            // Fallback: Alphabetisch
            return ascending
                ? cellA.localeCompare(cellB)
                : cellB.localeCompare(cellA);
        });

        // Tabelle neu anhängen
        sortedRows.forEach(row => tbody.appendChild(row));
    }


    const searchInput = document.getElementById("searchInput");

    searchInput.addEventListener("input", () => {
        const filter = searchInput.value.toLowerCase();
        const rows = table.querySelectorAll("tbody tr");

        rows.forEach(row => {
            const supplierCell = row.children[0]; // erste Spalte = Supplier
            const supplierName = supplierCell?.textContent.toLowerCase() || "";

            if (supplierName.includes(filter)) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    });

});

