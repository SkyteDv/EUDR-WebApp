const toggleIcon = document.getElementById('toggle-icon');
let isGrid = true;
const container = document.getElementById('harbour-card-container');

function switchLayout(switchTo) {
    if (switchTo === "grid") {
        container.classList.remove('list-layout');
    } else if (switchTo === "list") {
        container.classList.add('list-layout');
    }
}

document.getElementById('toggle-view-btn').addEventListener('click', () => {
    isGrid = !isGrid;
    toggleIcon.textContent = isGrid ? 'apps' : 'view_headline';
    let newLayout = isGrid ? "grid" : "list";
    switchLayout(newLayout);
});



