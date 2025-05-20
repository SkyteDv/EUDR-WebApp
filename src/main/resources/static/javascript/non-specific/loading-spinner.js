export function showLoadingOverlay() {
    let overlay = document.getElementById('dashboard-loading');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'dashboard-loading';
        overlay.classList.add('loading-overlay');
        overlay.dataset.dynamic = 'true';
        const spinner = document.createElement('div');
        spinner.classList.add('spinner');
        const loadingText = document.createElement('p');
        loadingText.textContent = 'Loading dashboard data...';
        overlay.appendChild(spinner);
        overlay.appendChild(loadingText);
        document.body.appendChild(overlay);
        requestAnimationFrame(() => overlay.classList.add('show'));
    } else {
        overlay.style.display = 'flex';
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
