google.charts.load('current', { packages: ['geochart'] });

google.charts.setOnLoadCallback(() => {
    const container = document.getElementById('regionsDiv');

    let chart;  // declared here, so accessible everywhere inside this callback
    let data;

    function isReady(el) {
        return el && el.offsetWidth > 0 && el.offsetHeight > 0;
    }

    async function fetchGeoData() {
        try {
            const res = await fetch('/api/geo-data');
            if (!res.ok) throw new Error('Network response was not ok');
            const jsonData = await res.json();

            const dataArray = [['Country', 'Active Deliveries ']];
            jsonData.forEach(item => {
                dataArray.push([item.country, item.deliveries]);
            });

            return dataArray;
        } catch (error) {
            console.error('Error fetching geo data:', error);
            return null;
        }
    }

    async function drawChart() {
        if (!isReady(container)) {
            setTimeout(drawChart, 100);
            return;
        }

        const dataArray = await fetchGeoData();
        if (!dataArray || dataArray.length <= 1) { // dataArray has header + rows, so <=1 means empty
            console.log('Data not ready, retrying in 1s...');
            setTimeout(drawChart, 1000); // retry after 1 second
            return;
        }

        data = google.visualization.arrayToDataTable(dataArray);
        const options = {
            colorAxis: {
                colors: [
                    '#afa7dc',
                    '#8e7cc3',
                    '#6857A8',
                    '#4b0082'
                ]
            }
        };

        chart = new google.visualization.GeoChart(container);
        chart.draw(data, options);

        google.visualization.events.addListener(chart, 'regionClick', (event) => {
            console.log('Region clicked:', event.region);
            onCountrySelected(event.region)
        });

    }

    function onCountrySelected(countryName) {
        // Find the row index for the selected country
        const rowIndex = data.getFilteredRows([{ column: 0, value: countryName }])[0];

        if (rowIndex !== undefined) {
            chart.setSelection([{ row: rowIndex }]);
        } else {
            // No country found — clear selection
            chart.setSelection([]);
        }

        const event = new CustomEvent('countrySelected', { detail: { countryName } });
        document.dispatchEvent(event);
        console.log('Selected country:', countryName);
    }

    // Retry drawing until container is ready
    function waitUntilReadyAndDraw() {
        if (isReady(container)) {
            drawChart();
        } else {
            setTimeout(waitUntilReadyAndDraw, 100);
        }
    }

    waitUntilReadyAndDraw();
});




