// Risk Management Actions JavaScript
// Handles modal interactions for the c-manage-specific-order.html page

document.addEventListener('DOMContentLoaded', function() {
    // Set up modal close functionality with escape key
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            closeAllModals();
        }
    });
    
    // Set up modal click-outside-to-close functionality
    window.addEventListener('click', function(event) {
        if (event.target.classList.contains('modal')) {
            closeModal(event.target.id);
        }
    });

    // Auto-hide success/error messages after 5 seconds
    hideMessagesAfterDelay();
});

// ========================================
// MODAL MANAGEMENT FUNCTIONS
// ========================================

function showProductGroupModal() {
    const modal = document.getElementById('productGroupModal');
    if (modal) {
        modal.style.display = 'block';
        // Focus on the select element for better UX
        setTimeout(() => {
            const select = document.getElementById('productGroupSelect');
            if (select) select.focus();
        }, 100);
    }
}

function showReroutingModal() {
    const modal = document.getElementById('reroutingModal');
    if (modal) {
        modal.style.display = 'block';
        // Focus on the select element for better UX
        setTimeout(() => {
            const select = document.getElementById('newDestination');
            if (select) select.focus();
        }, 100);
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
        // Clear form data when closing
        clearModalForms(modalId);
    }
}

function closeAllModals() {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        modal.style.display = 'none';
    });
    clearAllModalForms();
}

function clearModalForms(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        const selects = modal.querySelectorAll('select');
        const textareas = modal.querySelectorAll('textarea');
        
        selects.forEach(select => select.value = '');
        textareas.forEach(textarea => textarea.value = '');
    }
}

function clearAllModalForms() {
    const allSelects = document.querySelectorAll('.modal select');
    const allTextareas = document.querySelectorAll('.modal textarea');
    
    allSelects.forEach(select => select.value = '');
    allTextareas.forEach(textarea => textarea.value = '');
}

// ========================================
// MESSAGE HANDLING
// ========================================

function hideMessagesAfterDelay() {
    const messages = document.querySelectorAll('.alert');
    messages.forEach(message => {
        // Hide success messages after 4 seconds
        if (message.classList.contains('alert-success')) {
            setTimeout(() => {
                message.style.opacity = '0';
                setTimeout(() => message.remove(), 300);
            }, 4000);
        }
        // Hide error messages after 6 seconds
        else if (message.classList.contains('alert-error')) {
            setTimeout(() => {
                message.style.opacity = '0';
                setTimeout(() => message.remove(), 300);
            }, 6000);
        }
    });
}

// ========================================
// ACCESSIBILITY ENHANCEMENTS
// ========================================

// Add keyboard navigation for modals
document.addEventListener('keydown', function(event) {
    const activeModal = document.querySelector('.modal[style*="block"]');
    if (activeModal && event.key === 'Tab') {
        // Trap focus within modal
        const focusableElements = activeModal.querySelectorAll(
            'button, select, textarea, input, [tabindex]:not([tabindex="-1"])'
        );
        const firstElement = focusableElements[0];
        const lastElement = focusableElements[focusableElements.length - 1];
        
        if (event.shiftKey) {
            if (document.activeElement === firstElement) {
                event.preventDefault();
                lastElement.focus();
            }
        } else {
            if (document.activeElement === lastElement) {
                event.preventDefault();
                firstElement.focus();
            }
        }
    }
}); 