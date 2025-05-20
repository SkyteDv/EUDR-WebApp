document.getElementById('darkModeToggle').addEventListener('change', function () {
    localStorage.setItem('darkMode', this.checked);
});

document.getElementById('languageSelect').addEventListener('change', function () {
    localStorage.setItem('language', this.value);
});

document.getElementById('emailNotifications').addEventListener('change', function () {
    localStorage.setItem('emailNotifications', this.checked);
});

// Load settings on page load
window.addEventListener('DOMContentLoaded', () => {
    document.getElementById('darkModeToggle').checked = localStorage.getItem('darkMode') === 'true';
    document.getElementById('languageSelect').value = localStorage.getItem('language') || 'en';
    document.getElementById('emailNotifications').checked = localStorage.getItem('emailNotifications') === 'true';
});