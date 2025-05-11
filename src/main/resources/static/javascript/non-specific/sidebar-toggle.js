const toggleButton = document.getElementById('toggle-btn')
const sidebar = document.getElementById('sidebar')

function initSidebar() {
    toggleButton.classList.toggle('rotate')
}

function toggleSidebar(){
    sidebar.classList.toggle('open')
    toggleButton.classList.toggle('rotate')

    closeAllSubMenus()
}

function toggleSubMenu(button){

    if(!button.nextElementSibling.classList.contains('show')){
        closeAllSubMenus()
    }

    button.nextElementSibling.classList.toggle('show')
    button.classList.toggle('rotate')

    if(!sidebar.classList.contains('open')){
        sidebar.classList.toggle('open')
        toggleButton.classList.toggle('rotate')
    }
}

function closeAllSubMenus(){
    Array.from(sidebar.getElementsByClassName('show')).forEach(ul => {
        ul.classList.remove('show')
        ul.previousElementSibling.classList.remove('rotate')
    })
}

document.addEventListener('DOMContentLoaded', function () {
    initSidebar()
});

