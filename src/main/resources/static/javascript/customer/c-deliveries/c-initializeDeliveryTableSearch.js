document.addEventListener("DOMContentLoaded", function() {
    attachTableSearch('ordersTable', {
        columnsToSearch: [2,3,4,5,6,7,8,9,10,11,12],
        excludeRowsClass: 'exclude-from-search',
        highlight: true
    });
});