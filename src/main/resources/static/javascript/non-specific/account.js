const newPasswordInput = document.getElementById('newPassword');
const strengthBar = document.getElementById('strengthBar');
const strengthText = document.getElementById('strengthText');

newPasswordInput.addEventListener('input', () => {
    const val = newPasswordInput.value;
    const strength = calculateStrength(val);

    strengthBar.style.width = strength.percent + "%";
    strengthBar.style.backgroundColor = strength.color;
    strengthText.textContent = strength.text;
});

function calculateStrength(password) {
    let score = 0;
    if (password.length >= 8) score += 1;
    if (/[A-Z]/.test(password)) score += 1;
    if (/[0-9]/.test(password)) score += 1;
    if (/[^A-Za-z0-9]/.test(password)) score += 1;

    const levels = [
        { percent: 20, color: '#e74c3c', text: 'Very Weak' },
        { percent: 40, color: '#e67e22', text: 'Weak' },
        { percent: 60, color: '#f1c40f', text: 'Moderate' },
        { percent: 80, color: '#2ecc71', text: 'Strong' },
        { percent: 100, color: '#27ae60', text: 'Very Strong' }
    ];

    return levels[score] || levels[0];
}

document.querySelectorAll('.tab-link').forEach(button => {
            button.addEventListener('click', () => {
                const target = button.getAttribute('data-target');

                // Alle Tabs deaktivieren
                document.querySelectorAll('.tab-link').forEach(btn => btn.classList.remove('active'));
                button.classList.add('active');

                // Alle Sektionen verstecken
                document.querySelectorAll('.section').forEach(section => section.classList.remove('active'));
                document.getElementById(target).classList.add('active');
            });
        });
