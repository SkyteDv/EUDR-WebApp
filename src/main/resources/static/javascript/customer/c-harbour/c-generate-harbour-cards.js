import {hideLoadingOverlay, showLoadingOverlay} from '/javascript/non-specific/loading-spinner.js';

function createHarbourCardFull({
                                   harbourName = 'Placeholder',
                                   imageUrl = '/images/harbours/rotterdam.png',
                                   delivText = 'Placeholder',
                                   riskText = 'Placeholder',
                                   inStorageText = 'Placeholder'
                               } = {}) {
    // Create the outer div with class 'harbour-card'
    const harbourCardDiv = document.createElement('div');
    const bell = createBellIcon(riskText);
    if (bell) harbourCardDiv.appendChild(bell);

    harbourCardDiv.className = 'harbour-card';
    harbourCardDiv.setAttribute('data-harbour-name', harbourName);

    // Create the section with class 'card-content'
    const section = document.createElement('section');
    section.className = 'card-content';

    // Create card-content-actions div
    const actionsDiv = document.createElement('div');
    actionsDiv.className = 'card-content-actions';

    // Helper to create button with SVG and span
    function createButton(svgPath, spanId, spanText) {
        const button = document.createElement('button');

        const svgWrapper = document.createElement('div');
        svgWrapper.className = 'svg-wrapper';

        const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        svg.setAttribute('xmlns', "http://www.w3.org/2000/svg");
        svg.setAttribute('height', '24px');
        svg.setAttribute('width', '24px');
        svg.setAttribute('viewBox', '0 -960 960 960');
        svg.setAttribute('fill', 'black');

        const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
        path.setAttribute('d', svgPath);

        svg.appendChild(path);
        svgWrapper.appendChild(svg);
        button.appendChild(svgWrapper);

        const span = document.createElement('span');
        span.id = spanId;
        span.textContent = spanText;
        button.appendChild(span);

        return button;
    }

    // SVG paths for the buttons
    const svgPaths = {
        deliv: "M440-183v-274L200-596v274l240 139Zm80 0 240-139v-274L520-457v274Zm-40-343 237-137-237-137-237 137 237 137ZM160-252q-19-11-29.5-29T120-321v-318q0-22 10.5-40t29.5-29l280-161q19-11 40-11t40 11l280 161q19 11 29.5 29t10.5 40v318q0 22-10.5 40T800-252L520-91q-19 11-40 11t-40-11L160-252Zm320-228Z",
        risk: "M480-80q-139-35-229.5-159.5T160-516v-244l320-120 320 120v244q0 152-90.5 276.5T480-80Zm0-84q97-30 162-118.5T718-480H480v-315l-240 90v207q0 7 2 18h238v316Z",
        inStorage: "M120-40v-880h80v80h560v-80h80v880h-80v-80H200v80h-80Zm80-480h80v-160h240v160h240v-240H200v240Zm0 320h240v-160h240v160h80v-240H200v240Zm160-320h80v-80h-80v80Zm160 320h80v-80h-80v80ZM360-520h80-80Zm160 320h80-80Z"
    };

    // Add buttons to actionsDiv
    actionsDiv.appendChild(createButton(svgPaths.deliv, 'delivDisp', delivText));
    actionsDiv.appendChild(createButton(svgPaths.risk, 'riskDisp', riskText));
    actionsDiv.appendChild(createButton(svgPaths.inStorage, 'inStorageDisp', inStorageText));

    // Append actionsDiv to section
    section.appendChild(actionsDiv);

    // Create title container with h3
    const titleContainer = document.createElement('div');
    titleContainer.className = 'card-title-container';



    const h3 = document.createElement('h3');
    h3.className = 'card-title';
    h3.textContent = harbourName;

    titleContainer.appendChild(h3);
    section.appendChild(titleContainer);
    if (bell) titleContainer.appendChild(bell);

    // Append section to outer div
    harbourCardDiv.appendChild(section);

    // Create image element and append it
    const img = document.createElement('img');
    img.loading = "lazy";
    img.src = imageUrl;
    img.alt = 'Harbour Image';
    harbourCardDiv.appendChild(img);

    return harbourCardDiv;
}

const sortModes = [
    { label: 'Deliveries', key: 2, direction: 'desc' },
    { label: 'Risk', key: 3, direction: 'desc' },
    { label: 'Storage', key: 4, direction: 'desc' }
];

let currentSortIndex = 0;

function getCurrentSortMode() {
    return sortModes[currentSortIndex];
}

function cycleSortMode() {
    currentSortIndex = (currentSortIndex + 1) % sortModes.length;
}

function createBellIcon(riskText) {
    if (Number(riskText) <= 0) return null;

    const wrapper = document.createElement('div');
    wrapper.className = 'bell-wrapper';

    const bellIcon = document.createElement('div');
    bellIcon.className = 'bell-icon';
    bellIcon.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" height="30px" width="30px" viewBox="0 -960 960 960" fill="orange">
            <path d="m40-120 440-760 440 760H40Zm138-80h604L480-720 178-200Zm302-40q17 0 28.5-11.5T520-280q0-17-11.5-28.5T480-320q-17 0-28.5 11.5T440-280q0 17 11.5 28.5T480-240Zm-40-120h80v-200h-80v200Zm40-100Z"/>
        </svg>
    `;
    wrapper.appendChild(bellIcon);
    return wrapper;
}


function sortHarbours(data) {
    const { key, direction } = getCurrentSortMode();
    return Object.entries(data).sort((a, b) => {
        const aVal = Number(a[1][key]);
        const bVal = Number(b[1][key]);
        return direction === 'desc' ? bVal - aVal : aVal - bVal;
    });
}

function openHarbourDetails(harbourName) {
    if (!harbourName) return;
    window.location.href = `/customer/harbour/details/${encodeURIComponent(harbourName)}`;
}

async function fetchHarbours() {
    try {
        const response = await fetch("/api/harbour/get-harbours");
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();

        const container = document.getElementById('harbour-card-container');
        container.innerHTML = ''; // Clear previous content

        // Convert object to array and sort by deliveries (infoArray[2]) descending
        const sortedHarbours = sortHarbours(data)

        // Create cards from sorted data
        for (const [harbourName, infoArray] of sortedHarbours) {
            const card = createHarbourCardFull({
                harbourName,
                imageUrl: infoArray[0],
                delivText: infoArray?.[2] ?? 'N/A',
                riskText: infoArray?.[3] ?? 'N/A',
                inStorageText: infoArray?.[4] ?? 'N/A'
            });
            container.appendChild(card);

            card.addEventListener("click", () => {
                openHarbourDetails(harbourName);
            });
        }

    } catch (error) {
        console.error("Failed to fetch harbours:", error);
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    showLoadingOverlay();
    await fetchHarbours();
    hideLoadingOverlay();
});

document.getElementById('sortToggleBtn').addEventListener('click', async () => {
    cycleSortMode();
    const mode = getCurrentSortMode();
    document.getElementById('sortToggleBtn').textContent = `⬇ ${mode.label} `;
    showLoadingOverlay();
    await fetchHarbours();
    hideLoadingOverlay();
});