export function showLoadingOverlay(customText = 'Loading data...') {
    let overlay = document.getElementById('dashboard-loading');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'dashboard-loading';
        overlay.classList.add('loading-overlay');
        overlay.dataset.dynamic = 'true';

        const spinner = document.createElement('div');
        spinner.classList.add('spinner');

        const loadingText = document.createElement('p');
        loadingText.classList.add('loading-text');  // Add class for easier reference & styling
        loadingText.textContent = customText;

        overlay.appendChild(spinner);
        overlay.appendChild(loadingText);
        document.body.appendChild(overlay);

        requestAnimationFrame(() => overlay.classList.add('show'));
    } else {
        overlay.style.display = 'flex';
        // Update existing loading text
        const loadingText = overlay.querySelector('.loading-text');
        if (loadingText) loadingText.textContent = customText;

        requestAnimationFrame(() => overlay.classList.add('show'));
    }
}

export function hideLoadingOverlay() {
    const overlay = document.getElementById('dashboard-loading');
    if (!overlay) return;

    overlay.classList.remove('show');
    if (overlay.dataset.dynamic === 'true') {
        overlay.addEventListener('transitionend', () => overlay.remove(), { once: true });
    } else {
        overlay.style.display = 'none';
    }
}
