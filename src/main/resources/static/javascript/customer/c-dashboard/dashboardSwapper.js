import { showLoadingOverlay, hideLoadingOverlay } from '/javascript/non-specific/loading-spinner.js';


const rootStyles = getComputedStyle(document.documentElement);
const baseAccColor = rootStyles.getPropertyValue('--base-acc-clr').trim();
const fieldHoverColor = rootStyles.getPropertyValue('--div-acc-purple-clr').trim();
const buttonHover = rootStyles.getPropertyValue('--button-hover-bg-clr').trim();

let swapper_globalBtn = document.getElementById("swapper-global");
let swapper_countryBtn = document.getElementById("swapper-country");
const statSections = document.querySelectorAll(".stat-section");

const countryDataCache = new Map();
let currentCountry = null;

async function fetchCountryData() {
    try {
        const response = await fetch('/api/dashboard-data/country-data');
        if (!response.ok) throw new Error('Failed to fetch country data');

        const data = await response.json();

        // Assuming data comes as { "DE": { attr1: val1, ... }, "FR": { ... }, ... }
        for (const [iso, stats] of Object.entries(data)) {
            countryDataCache.set(iso, stats);
        }

        console.log('Country data loaded into cache');
    } catch (error) {
        console.error('Error loading country data:', error);
    }
}

function setSpansNA() {
    const allSpans = document.querySelectorAll('.stat-section .stat-card span');
    allSpans.forEach(span => span.textContent = 'N/A');
}

function changeToGlobal() {
    statSections.forEach(section => {
        section.style.backgroundColor = baseAccColor;
    });
    displayStats("GLOBAL");
}

function changeToCountry() {
    statSections.forEach(section => {
        section.style.backgroundColor = fieldHoverColor;
    });
    if (currentCountry) {
        displayStats(currentCountry);
    } else {
        setSpansNA();
    }
}

function displayStats(key) {
    if (key !== "GLOBAL") {
        currentCountry = key;
    }
    const countryStats = countryDataCache.get(key);


    if (!countryStats) {
        setSpansNA();
        console.warn(`No data found for Entry: ${key}`);
        return;
    }

    const statMapping = {
        'deliveries.activeTotal': '#deliveries-active-total span',
        'deliveries.completedTotal': '#deliveries-completed-total span',
        'deliveries.activeHighRisk': '#deliveries-active-high-risk span',
        'deliveries.highRiskPercentage': '#deliveries-high-risk-percentage span',
        'suppliers.activeTotal': '#suppliers-active-total span',
        'suppliers.historicalTotal': '#suppliers-historical-total span',
        'dds.greenRate': '#dds-green-rate span',
        'dds.attachRate': '#dds-attachRate span'
    };

    for (const [key, selector] of Object.entries(statMapping)) {
        const element = document.querySelector(selector);
        if (element) {
            element.textContent = countryStats[key] ?? 'N/A';
        }
    }

    console.log("Updated stats for", key);
}


swapper_globalBtn.addEventListener('click', changeToGlobal);
swapper_countryBtn.addEventListener('click', changeToCountry);

document.addEventListener('countrySelected', (e) => {
    const countryIso = e.detail.countryName;

    const regionNames = new Intl.DisplayNames(['en'], { type: 'region' });
    swapper_countryBtn.querySelector('span').textContent = regionNames.of(countryIso);

    // Switch view and update UI
    changeToCountry();
    displayStats(countryIso);
});

async function waitForImportToComplete(timeout = 30000, interval = 1000) {
    const startTime = Date.now();

    while (Date.now() - startTime < timeout) {
        try {
            const res = await fetch('/api/import-status');
            const isDone = await res.json();

            if (isDone) {
                console.log('✅ Import complete');
                return;
            }

            console.log('⏳ Waiting for import...');
        } catch (err) {
            console.error('Error checking import status:', err);
        }

        await new Promise(resolve => setTimeout(resolve, interval));
    }

    console.warn('⚠️ Timed out waiting for import to complete');
}

document.addEventListener('DOMContentLoaded', async () => {
    showLoadingOverlay("Loading Dashboard Data...");
    setSpansNA();
    await waitForImportToComplete();
    await fetchCountryData();
    changeToGlobal();
    hideLoadingOverlay();
});
