document.addEventListener("DOMContentLoaded", function () {
  const toggle = document.getElementById("account-toggle");
  const menu = document.getElementById("account-menu");

  if (toggle && menu) {
    toggle.addEventListener("click", function (event) {
      event.preventDefault();
      menu.style.display = menu.style.display === "block" ? "none" : "block";
    });

    window.addEventListener("click", function (e) {
      if (!toggle.contains(e.target) && !menu.contains(e.target)) {
        menu.style.display = "none";
      }
    });
  }
});

