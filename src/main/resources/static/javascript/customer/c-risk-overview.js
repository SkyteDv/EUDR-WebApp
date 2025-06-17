document.addEventListener('DOMContentLoaded', function() {
    // Initialize donut chart
    initializeDonutChart();
});

function initializeDonutChart() {
    const donutChart = document.querySelector('.donut-chart');
    if (!donutChart) return;

    // Get data from attributes
    const highPercentage = parseFloat(donutChart.getAttribute('data-high')) || 0;
    const mediumPercentage = parseFloat(donutChart.getAttribute('data-medium')) || 0;
    const lowPercentage = parseFloat(donutChart.getAttribute('data-low')) || 0;
    const unknownPercentage = parseFloat(donutChart.getAttribute('data-unknown')) || 0;

    // Calculate angles (360 degrees total)
    const highAngle = (highPercentage / 100) * 360;
    const mediumAngle = highAngle + (mediumPercentage / 100) * 360;
    const lowAngle = mediumAngle + (lowPercentage / 100) * 360;

    // Set CSS custom properties
    donutChart.style.setProperty('--high-angle', `${highAngle}deg`);
    donutChart.style.setProperty('--medium-angle', `${mediumAngle}deg`);
    donutChart.style.setProperty('--low-angle', `${lowAngle}deg`);

    // Update the conic-gradient background
    let gradientStops = [];
    let currentAngle = 0;

    if (highPercentage > 0) {
        gradientStops.push(`#dc3545 ${currentAngle}deg ${currentAngle + highAngle}deg`);
        currentAngle += highAngle;
    }

    if (mediumPercentage > 0) {
        gradientStops.push(`#ffc107 ${currentAngle}deg ${currentAngle + (mediumPercentage / 100) * 360}deg`);
        currentAngle += (mediumPercentage / 100) * 360;
    }

    if (lowPercentage > 0) {
        gradientStops.push(`#28a745 ${currentAngle}deg ${currentAngle + (lowPercentage / 100) * 360}deg`);
        currentAngle += (lowPercentage / 100) * 360;
    }

    if (unknownPercentage > 0) {
        gradientStops.push(`#6c757d ${currentAngle}deg 360deg`);
    }

    // If no data, show a neutral grey
    if (gradientStops.length === 0) {
        gradientStops.push('#e9ecef 0deg 360deg');
    }

    const gradient = `conic-gradient(${gradientStops.join(', ')})`;
    donutChart.style.background = gradient;
} 