document.addEventListener("DOMContentLoaded", function() {
    attachTableSearch('activeDeliveriesTable', {
        columnsToSearch: [],
        excludeRowsClass: 'exclude-from-search',
        highlight: true
    });
});

