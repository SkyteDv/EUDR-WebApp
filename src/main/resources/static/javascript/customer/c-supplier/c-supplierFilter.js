const supplierFilterBtn = document.getElementById('supplierFilterBtn');
const supplierFilterDiv = document.getElementById('supplierFilterDiv');
const backdrop = document.getElementById('backdrop');

supplierFilterBtn.addEventListener('click', () => {
    supplierFilterDiv.classList.toggle('open');
    backdrop.classList.toggle('active');
});

backdrop.addEventListener('click', () => {
    supplierFilterDiv.classList.remove('open');
    backdrop.classList.remove('active');
});

document.getElementById('resetSupplierFilter').addEventListener('click', () => {
    document.getElementById('supplierNameFilter').value = '';
    document.getElementById('minGreenRate').value = '';
    document.getElementById('maxRedDeliveries').value = '';
    // ggf. Filterlogik hier rücksetzen
});

document.getElementById('applySupplierFilter').addEventListener('click', () => {
    const name = document.getElementById('supplierNameFilter').value.toLowerCase();
    const minGreen = parseFloat(document.getElementById('minGreenRate').value);
    const maxRed = parseInt(document.getElementById('maxRedDeliveries').value);

    document.querySelectorAll('table tbody tr').forEach(row => {
        const nameText = row.children[0].innerText.toLowerCase();
        const greenRateText = row.children[5].innerText.replace('%', '');
        const redDeliveries = parseInt(row.children[3].innerText);

        let visible = true;

        if (name && !nameText.includes(name)) visible = false;
        if (!isNaN(minGreen) && parseFloat(greenRateText) < minGreen) visible = false;
        if (!isNaN(maxRed) && redDeliveries > maxRed) visible = false;

        row.style.display = visible ? '' : 'none';
    });

    supplierFilterDiv.classList.remove('open');
    backdrop.classList.remove('active');
});

