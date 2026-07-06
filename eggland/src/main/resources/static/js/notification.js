(function() {
    let lastCount = 0;
    let pollInterval = null;

    function getBadge() {
        return document.querySelector('.notification-badge');
    }

    function getPanel() {
        return document.querySelector('.notification-panel');
    }

    function getList() {
        return document.querySelector('.notification-list');
    }

    function updateBadge(count) {
        const badge = getBadge();
        if (!badge) return;
        if (count > 0) {
            badge.textContent = count;
            badge.style.display = 'flex';
        } else {
            badge.style.display = 'none';
        }
    }

    function showToast(message) {
        const existing = document.querySelector('.notification-toast');
        if (existing) existing.remove();

        const toast = document.createElement('div');
        toast.className = 'notification-toast';
        toast.innerHTML = '<i class="fa-solid fa-bell"></i><span></span>';
        toast.querySelector('span').textContent = message;

        document.body.appendChild(toast);

        requestAnimationFrame(function() { toast.classList.add('show'); });

        setTimeout(function() {
            toast.classList.remove('show');
            setTimeout(function() { toast.remove(); }, 300);
        }, 5000);

        toast.addEventListener('click', function() {
            toast.classList.remove('show');
            setTimeout(function() { toast.remove(); }, 300);
        });
    }

    function checkNewNotifications() {
        fetch('/api/notifications/unread/count')
            .then(r => r.json())
            .then(data => {
                const count = data.count;
                if (count > lastCount && lastCount > 0) {
                    fetch('/api/notifications/unread')
                        .then(r => r.json())
                        .then(notifs => {
                            if (notifs.length > 0) {
                                showToast(notifs[0].message);
                            }
                        });
                }
                lastCount = count;
                updateBadge(count);
            });
    }

    function refreshList() {
        const list = getList();
        if (!list || !list.closest('.notification-panel.open')) return;

        fetch('/api/notifications/unread')
            .then(r => r.json())
            .then(notifs => {
                if (notifs.length === 0) {
                    list.innerHTML = '<div class="notification-empty">Aucune notification</div>';
                    updateBadge(0);
                    return;
                }
                const html = notifs.map(n => {
                    const time = new Date(n.dateCreation);
                    const formatted = time.toLocaleDateString('fr-FR') + ' ' + time.toLocaleTimeString('fr-FR', {hour: '2-digit', minute: '2-digit'});
                    return '<div class="notification-item" data-id="' + n.id + '">' +
                        '<span class="notification-time">' + formatted + '</span>' +
                        '<span class="notification-message">' + n.message + '</span>' +
                        '</div>';
                }).join('');
                list.innerHTML = html;

                list.querySelectorAll('.notification-item').forEach(item => {
                    item.addEventListener('click', function() {
                        const id = this.dataset.id;
                        fetch('/api/notifications/' + id + '/read', { method: 'POST' })
                            .then(() => {
                                this.remove();
                                const remaining = list.querySelectorAll('.notification-item').length;
                                if (remaining === 0) {
                                    list.innerHTML = '<div class="notification-empty">Aucune notification</div>';
                                }
                                checkNewNotifications();
                            });
                    });
                });
            });
    }

    function addMarkAllButton() {
        const panel = getPanel();
        if (!panel) return;
        const header = panel.querySelector('.notification-header');
        if (!header || header.querySelector('.mark-all-read')) return;

        const btn = document.createElement('button');
        btn.className = 'mark-all-read';
        btn.textContent = 'Tout marquer comme lu';
        btn.addEventListener('click', function() {
            fetch('/api/notifications/read-all', { method: 'POST' })
                .then(() => {
                    const list = getList();
                    if (list) list.innerHTML = '<div class="notification-empty">Aucune notification</div>';
                    updateBadge(0);
                    lastCount = 0;
                });
        });
        header.appendChild(btn);
    }

    document.addEventListener('DOMContentLoaded', function() {
        const badge = getBadge();
        if (badge) {
            lastCount = parseInt(badge.textContent) || 0;
        }

        addMarkAllButton();

        pollInterval = setInterval(checkNewNotifications, 30000);

        const btn = document.querySelector('.notification-btn');
        if (btn) {
            btn.addEventListener('click', function() {
                setTimeout(refreshList, 100);
            });
        }

        document.addEventListener('click', function(e) {
            const panel = getPanel();
            if (panel && panel.classList.contains('open') && !e.target.closest('.notification-dropdown')) {
                setTimeout(refreshList, 50);
            }
        });
    });
})();
