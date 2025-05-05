document.addEventListener("DOMContentLoaded", function() {
    attachTableSearch('ordersTable', {
        columnsToSearch: [],
        excludeRowsClass: 'exclude-from-search',
        highlight: true
    });
});