// === DOM ELEMENTS ===
const sidePopWrapper = document.getElementById("wrapper-side-popup")
const sidePop = document.getElementById("side-popup-details");
const closeBtn = document.getElementById("closeBtn");
const statusPopup = document.getElementById("statusPopup");
const statusPopupCloseBtn = document.getElementById("statusPopupCloseBtn");
const title = document.getElementById("popup-title");

const manageRiskBtn = document.getElementById("manageRiskBtn");

// === STATE ===
let currentRow = null;
let currentOrderDdsReference = null;
let currentOrderId = null;
let isTransitioning = false;

// === SIDEBAR LOGIC ===
function openSidebar() {
    sidePop.style.visibility = 'visible';
    sidePop.classList.add('popen');
}

function closeSidebar() {
    if (!sidePop.classList.contains('wide')) {
        sidePop.classList.remove("popen");
        sidePop.addEventListener('transitionend', function handler() {
            sidePop.style.visibility = 'hidden';
            sidePop.removeEventListener('transitionend', handler);
        });
        if (currentRow) currentRow.classList.remove("selected");
    } else {
        sidePop.classList.remove('wide')
    }
}

// === POPUP SETUP ===
function openSidebarFromRow(row) {
    if (currentRow) currentRow.classList.remove('selected');

    currentRow = row;
    currentOrderId = row.dataset.id;

    row.classList.add('selected');

    const allKeys = ['id',
        'erpreferencenumber',
        'ddsstatus',
        'orderdate',
        'estimateddeliverydate',
        'dimensions',
        'supplier',
        'supplierlocation',
        'productcategory',
        'productname'];

    allKeys.forEach(key => {
        const span = document.getElementById("span-" + key);
        if (span) span.textContent = "";
    });

    const span = document.getElementById("span-" + 'ddsreferencenumber');
    if (span) span.textContent = "Denied";

    for (const key in row.dataset) {
        const span = document.getElementById("span-" + key.toLowerCase());
        if (span) {
            span.textContent = row.dataset[key];  // then fill
        }
    }

    const riskLevel = row.dataset.risklevel ? row.dataset.risklevel.toUpperCase() : "UNKNOWN";

    setupPopupButtons(riskLevel);
    openSidebar();
}

function setupPopupButtons(riskLevel) {
    if (manageRiskBtn) {
        if (riskLevel === "MEDIUM" || riskLevel === "HIGH") {
            manageRiskBtn.classList.add("pulsate");
        } else {
            manageRiskBtn.classList.remove("pulsate");
        }
    }
}

// === GLOBAL EVENTS ===
closeBtn.addEventListener('click', closeSidebar);

manageRiskBtn.addEventListener('click', () => {
    window.location.href = `/customer/risk/manage-order/${currentOrderId}`;
});

document.addEventListener("keydown", function(event) {
    if (event.key === "Escape") {
        if (isTransitioning) {
            console.log("Ignoring ESC during transition");
            return;
        }
        closeSidebar();
    }
});

// === TABLE ROW LISTENERS ===
document.querySelectorAll(".clickable-row").forEach(row => {
    if(row.classList.contains("red-outline")) {
        return;
    }
    row.addEventListener("click", () => openSidebarFromRow(row));
});
