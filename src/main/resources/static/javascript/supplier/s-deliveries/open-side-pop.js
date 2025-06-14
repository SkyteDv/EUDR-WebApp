import { createCameraPopup } from './generateCameraDiv.js';

// === DOM ELEMENTS ===
const sidePopWrapper = document.getElementById("wrapper-side-popup")
const sidePop = document.getElementById("side-popup-details");
const closeBtn = document.getElementById("closeBtn");
const statusPopup = document.getElementById("statusPopup");
const statusPopupCloseBtn = document.getElementById("statusPopupCloseBtn");
const title = document.getElementById("popup-title");

const noteBtn = document.getElementById("noteBtn");
const dangerBtn = document.getElementById("dangerBtn");
const verifyBtn = document.getElementById("verifyBtn");
const doneBtn = document.getElementById("doneBtn");

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
    if(sidePop.classList.contains('wide')){
        const existingCameraDiv = document.getElementById("cameraDiv");
        const verifyButton = document.getElementById("verifyBtn");
        verifyButton.removeAttribute("disabled");
        verifyButton.style.display = 'inline-flex';
        title.innerText = 'Detail View';
        if (existingCameraDiv) {
            existingCameraDiv.remove();
        }
        sidePop.classList.remove('wide')
        return;
    }

    sidePop.classList.remove("popen");


    sidePop.addEventListener('transitionend', function handler() {
        sidePop.style.visibility = 'hidden';
        sidePop.removeEventListener('transitionend', handler);
    });

    doneBtn.style.display = "none";
    if (currentRow) currentRow.classList.remove("selected");
}

// === POPUP SETUP ===
function openSidebarFromRow(row) {
    if (currentRow) currentRow.classList.remove('selected');

    currentRow = row;
    currentOrderId = row.dataset.id;
    currentOrderDdsReference = row.dataset.ddsreferencenumber;

    row.classList.add('selected');

    const allKeys = ['id', 'erpreferencenumber', 'ddsondeliverynote', 'orderdate', 'estimateddeliverydate', 'orderdimensions', 'customername', 'customerlocation', 'productcategory', 'productname'];

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

    setupPopupButtons();
    openSidebar();
}

function setupPopupButtons() {
    // Clean previous listeners
    noteBtn.replaceWith(noteBtn.cloneNode(true));
    dangerBtn.replaceWith(dangerBtn.cloneNode(true));
    verifyBtn.replaceWith(verifyBtn.cloneNode(true));
    doneBtn.replaceWith(doneBtn.cloneNode(true));

    // Re-query updated buttons
    const freshNoteBtn = document.getElementById("noteBtn");
    const freshDangerBtn = document.getElementById("dangerBtn");
    const freshVerifyBtn = document.getElementById("verifyBtn");
    const freshDoneBtn = document.getElementById("doneBtn");

    if (!currentOrderId) {
        console.warn("No current order ID set.");
        return;
    }

    // === NOTE BUTTON ===
    freshNoteBtn.addEventListener('click', () => {
        window.location.href = `/api/generate-order-pdf/${currentOrderId}`;
    });

    // === DANGER BUTTON ===
    freshDangerBtn.addEventListener('click', () => {
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
    });

    freshVerifyBtn.addEventListener('click', () => {
        if (!sidePop.classList.contains("popen")) return;

        freshVerifyBtn.setAttribute("disabled", "");
        freshVerifyBtn.style.display = "none";
        sidePop.classList.add("wide");
        title.textContent = "Verification";
        doneBtn.style.display = "inline-flex";

        isTransitioning = true;

        sidePop.addEventListener('transitionend', function handler(e) {
            if (e.propertyName !== "width") return;
            isTransitioning = false;
            sidePop.removeEventListener('transitionend', handler);

            // ⛔️ Sidebar was closed during transition — abort cameraDiv creation
            if (!sidePop.classList.contains("popen")) return;

            // Remove old camera div if it exists
            removeExistingCameraDiv();
            console.log("Verify Clicked")
            // Create the new popup
            createCameraPopup(currentOrderId, currentOrderDdsReference);
        });
    });


    // === DONE BUTTON ===
    freshDoneBtn.addEventListener('click', () => {
        sidePop.classList.remove("wide", "popen");

        sidePop.addEventListener('transitionend', function handler() {
            sidePop.style.visibility = 'hidden';
            sidePop.removeEventListener('transitionend', handler);
        });

        doneBtn.style.display = "none";

        if (currentRow) {
            currentRow.classList.remove("selected");
        }
    });
}

// === STATUS POPUP ===
function showStatusPopup() {
    statusPopup.classList.remove("hidden");
    statusPopup.classList.add("visible");
}

statusPopupCloseBtn.addEventListener("click", () => {
    statusPopup.classList.remove("visible");
    window.location.reload();
});

// === GLOBAL EVENTS ===
closeBtn.addEventListener('click', closeSidebar);

document.addEventListener("keydown", function(event) {
    if (event.key === "Escape") {
        if (isTransitioning) {
            console.log("Ignoring ESC during transition");
            return;
        }
        closeSidebar();
    }
});

function removeExistingCameraDiv() {
    const oldCameraDiv = document.getElementById("cameraDiv");
    if (oldCameraDiv) {
        oldCameraDiv.remove();
    }
}

// === TABLE ROW LISTENERS ===
document.querySelectorAll(".clickable-row").forEach(row => {
    /*
    if(row.classList.contains("red-outline")) {
        return;
    }
     */
    row.addEventListener("click", () => openSidebarFromRow(row));
});
