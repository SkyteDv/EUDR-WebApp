function toggleTable() {
    const rightContent = document.getElementById("right-content");
    const tableWrapper = document.getElementById("table-wrapper");
    const toggleBtn = document.getElementById("table-toggle");
    const arrowSvg = document.getElementById("arrow-svg");
    const chartContainer = document.getElementById("step-chart");

    if (!rightContent.classList.contains("open")) {
        // Opening
        if (chartContainer) {
            chartContainer.style.display = "none";
        }
        tableWrapper.classList.remove("hidden");
        rightContent.classList.add("open");
        tableWrapper.classList.add("open");
        arrowSvg.classList.remove("rotated");
    } else {
        // Closing
        rightContent.classList.remove("open");
        arrowSvg.classList.add("rotated");

        // Listen once for transition end on tableWrapper
        const onTransitionEnd = (event) => {
            if (event.propertyName === "width") {
                tableWrapper.classList.add("hidden");
                tableWrapper.removeEventListener("transitionend", onTransitionEnd);
            }

            if (chartContainer) {
                chartContainer.style.display = "block";
            }
        };

        tableWrapper.addEventListener("transitionend", onTransitionEnd);
        tableWrapper.classList.remove("open");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const toggleBtn = document.getElementById("table-toggle");
    const arrowSvg = document.getElementById("arrow-svg");
    const tableWrapper = document.getElementById("table-wrapper");

    tableWrapper.classList.add("hidden");
    arrowSvg.classList.add("rotated");

    toggleBtn.addEventListener("click", toggleTable);
});
