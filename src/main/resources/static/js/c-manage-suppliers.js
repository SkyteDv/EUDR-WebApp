const searchInput = document.getElementById('searchInput');
const supplierList = document.getElementById('supplierList');
const items = supplierList.getElementsByTagName('li');

searchInput.addEventListener('input', function () {
    const filter = searchInput.value.toLowerCase();
    Array.from(items).forEach(function (item) {
        const name = item.textContent.toLowerCase();
        item.style.display = name.includes(filter) ? '' : 'none';
    });
});