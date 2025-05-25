const openFilterBtn = document.getElementById("openFilterBtn");
const filterSideBar = document.getElementById("side-popup-details");
const backdrop = document.getElementById("backdrop");

const closeBtn = document.getElementById("closeBtn");
const applyBtn = document.getElementById("applyBtn");
const resetBtn = document.getElementById("resetBtn");

function setupPopupButtons() {

}

function openFilterSidebar() {
    filterSideBar.style.visibility = 'visible';
    filterSideBar.classList.add('popen');
    backdrop.classList.toggle('active');
    setupPopupButtons();
}

function closeFilterSidebar() {
    filterSideBar.classList.remove('popen');
    backdrop.classList.toggle('active');

    filterSideBar.addEventListener('transitionend', function handler() {
        filterSideBar.style.visibility = 'hidden';
        filterSideBar.removeEventListener('transitionend', handler);
    });
}

openFilterBtn.addEventListener("click", function() {
    openFilterSidebar();
})

function applyFilters() {

}

applyBtn.addEventListener("click", function() {
    applyFilters();
    closeFilterSidebar();
});

function resetFilters() {

}

resetBtn.addEventListener("click", function() {
    resetFilters();
})

closeBtn.addEventListener('click', closeFilterSidebar);

document.addEventListener("keydown", function(event) {
    if (event.key === "Escape") {
        if (isTransitioning) {
            console.log("Ignoring ESC during transition");
            return;
        }
        closeSidebar();
    }
});