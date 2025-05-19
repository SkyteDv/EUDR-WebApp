document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("searchInput");
    const table = document.getElementById("customersTable");

    if (!searchInput || !table) return;

    searchInput.addEventListener("input", function () {
        const filter = searchInput.value.toLowerCase();
        const rows = table.querySelectorAll("tbody tr");

        rows.forEach(row => {
            const cells = row.querySelectorAll("td");
            const match = Array.from(cells).some(td =>
                td.textContent.toLowerCase().includes(filter)
            );
            row.style.display = match ? "" : "none";
        });
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const table = document.getElementById("customersTable");
    const headers = table.querySelectorAll("th.sortable");
    let sortDirection = {}; // Speicherung der Richtung je Spalte

    headers.forEach((header, columnIndex) => {
        header.style.cursor = "pointer";
        const icon = document.createElement("i");
        icon.className = "sort-icon fas fa-sort";
        header.appendChild(icon);

        header.addEventListener("click", () => {
            const tbody = table.querySelector("tbody");
            const rows = Array.from(tbody.querySelectorAll("tr"));
            const isAscending = !sortDirection[columnIndex];

            rows.sort((a, b) => {
                const aText = a.children[columnIndex].innerText.trim();
                const bText = b.children[columnIndex].innerText.trim();

                // Zahlenerkennung, sonst Stringvergleich
                const aVal = isNaN(aText) ? aText.toLowerCase() : parseFloat(aText);
                const bVal = isNaN(bText) ? bText.toLowerCase() : parseFloat(bText);

                if (aVal < bVal) return isAscending ? -1 : 1;
                if (aVal > bVal) return isAscending ? 1 : -1;
                return 0;
            });

            // Neuanordnung im DOM
            rows.forEach(row => tbody.appendChild(row));

            // Icons aktualisieren
            headers.forEach((h, i) => {
                const icon = h.querySelector(".sort-icon");
                if (i === columnIndex) {
                    icon.className = `sort-icon fas ${isAscending ? 'fa-sort-up' : 'fa-sort-down'}`;
                } else {
                    icon.className = "sort-icon fas fa-sort";
                }
            });

            sortDirection[columnIndex] = isAscending;
        });
    });
});
