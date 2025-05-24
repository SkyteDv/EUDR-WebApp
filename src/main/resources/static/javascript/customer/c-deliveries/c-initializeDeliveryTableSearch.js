document.addEventListener("DOMContentLoaded", function() {
    attachTableSearch('activeDeliveriesTable', {
        columnsToSearch: [3,4,5,6,7,8,9],
        excludeRowsClass: 'exclude-from-search',
        highlight: true
    });
});