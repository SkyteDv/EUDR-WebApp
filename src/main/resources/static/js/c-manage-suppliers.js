document.addEventListener("DOMContentLoaded", function () {
    const allSupplierList = document.getElementById("allSupplierList");
    const mySupplierList = document.getElementById("mySupplierList");

    // Attach event listener to each supplier in All Suppliers
    allSupplierList.querySelectorAll(".supplier-item").forEach(item => {
        item.addEventListener("click", handleSupplierClick);
    });

    function handleSupplierClick(event) {
        const clickedItem = event.currentTarget;
        const supplierId = clickedItem.dataset.id;

        fetch("/suppliers/select", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ supplierId: supplierId })
        })
            .then(response => response.text())
            .then(data => {
                console.log(data);

                // Clone the supplier and remove the event listener via double-clone
                const cleanClone = clickedItem.cloneNode(true);
                const finalItem = cleanClone.cloneNode(true); // ensures no event listeners

                // Append the cleaned supplier to My Suppliers list
                mySupplierList.appendChild(finalItem);

                // Remove the original element from All Suppliers list
                clickedItem.remove();
            });
    }

});
