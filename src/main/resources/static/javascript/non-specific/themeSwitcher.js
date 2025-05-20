function toggleTheme(isLightMode) {
    const darkModeSheets = [
        '/css/non-specific/landing.css',
        '/css/non-specific/landingDesign.css',
        '/css/non-specific/login.css',
        '/css/non-specific/register.css'
    ];

    const lightModeSheets = [
        '/css/non-specific/landingLightmode.css',
        '/css/non-specific/switch.css'
    ];

    document.querySelectorAll('link[rel="stylesheet"]').forEach(link => {
        const href = link.getAttribute('href');
        if (!href) return;

        if (darkModeSheets.includes(href)) {
            link.disabled = isLightMode;
        }
        if (lightModeSheets.includes(href)) {
            link.disabled = !isLightMode;
        }
    });

    // Speichern im localStorage (wird über Seitenwechsel hinweg behalten)
    localStorage.setItem('folium-theme', isLightMode ? 'light' : 'dark');

    // Checkbox synchronisieren, falls vorhanden
    const switchInput = document.getElementById('theme-switch');
    if (switchInput) switchInput.checked = isLightMode;
}

document.addEventListener('DOMContentLoaded', () => {
    const saved = localStorage.getItem('folium-theme');
    const isLight = saved === 'light' || saved === null;
    toggleTheme(isLight);

    const switchInput = document.getElementById('theme-switch');
    if (switchInput) {
        switchInput.checked = isLight;
        switchInput.addEventListener('change', () => {
            toggleTheme(switchInput.checked);
        });
    }
});
