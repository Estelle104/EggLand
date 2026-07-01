document.addEventListener('DOMContentLoaded', function() {
    const notificationBtn = document.querySelector('.notification-btn');
    const notificationPanel = document.querySelector('.notification-panel');

    if (notificationBtn && notificationPanel) {
        notificationBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            notificationPanel.classList.toggle('open');
            notificationBtn.setAttribute('aria-expanded', notificationPanel.classList.contains('open'));
        });

        document.addEventListener('click', function(e) {
            if (!notificationBtn.contains(e.target) && !notificationPanel.contains(e.target)) {
                notificationPanel.classList.remove('open');
                notificationBtn.setAttribute('aria-expanded', 'false');
            }
        });

        notificationPanel.addEventListener('click', function(e) {
            e.stopPropagation();
        });
    }
});