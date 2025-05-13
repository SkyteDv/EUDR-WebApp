const closeBtn = document.getElementById("closeBtn");
const sidePop = document.getElementById("side-popup-details");

let currentRow = null;
let currentOrderId = null;

function openSidebar() {
    sidePop.style.visibility = 'visible';
    sidePop.classList.add('open');
}

function closeSidebar() {
    sidePop.classList.remove('open');

    if (currentRow) {
        currentRow.classList.remove('selected');
    }

    sidePop.addEventListener('transitionend', function handler() {
        sidePop.style.visibility = 'hidden';
        sidePop.removeEventListener('transitionend', handler);
    });
}

closeBtn.addEventListener('click', closeSidebar);

function openSidebarFromRow(row) {
    if (currentRow) {
        currentRow.classList.remove('selected');
    }

    currentRow = row;
    currentOrderId = row.dataset.id; // ✅ Grab order ID from dataset

    row.classList.add('selected');

    console.log("Clicked row dataset:", row.dataset);

    // Fill popup content
    for (const key in row.dataset) {
        const span = document.getElementById("span-" + key.toLowerCase());
        if (span) {
            span.textContent = row.dataset[key];
        }
    }

    setupPopupButtons(); // ✅ now currentOrderId is set before calling this
    openSidebar();
}

function showStatusPopup() {
    const popup = document.getElementById("statusPopup");
    popup.classList.remove("hidden");
    popup.classList.add("visible");
}

document.getElementById("statusPopupCloseBtn").addEventListener("click", () => {
    const popup = document.getElementById("statusPopup");
    popup.classList.remove("visible");
    window.location.reload();
});


function setupPopupButtons() {
    const noteBtn = document.getElementById("noteBtn");
    const dangerBtn = document.getElementById("dangerBtn");

    if (!currentOrderId) {
        console.warn("No current order ID set.");
        return;
    }

    if (noteBtn) {
        noteBtn.onclick = () => {
            window.location.href = `/api/generate-order-pdf/${currentOrderId}`;
        };
    }

    if (dangerBtn) {
        dangerBtn.onclick = () => {
            const confirmed = confirm("Are you sure you want to mark this Order as sent?\nThis will negatively impact your Credibility!");
            if (!confirmed) return;
            fetch("/api/deliveries/update/status", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    orderId: currentOrderId,
                    status: "SHIPPED"
                })
            })
                .then(res => res.json())
                .then(data => {
                    console.log(data.message || "Status updated.");
                    showStatusPopup();
                })
                .catch(err => {
                    console.error("Failed to update status:", err);
                    alert("Failed to update status.");
                });
        };
    }
}

document.querySelectorAll(".clickable-row").forEach(row => {
    row.addEventListener("click", () => openSidebarFromRow(row));
});

document.addEventListener("keydown", function(event) {
    if (event.key === "Escape") {
        closeSidebar();
    }
});
