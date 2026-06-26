(function () {
    'use strict';

    var ID_POULE = 2;
    var ligneIndex = 0;

    function creerDropdownProduits(inputProduitId, triggerProduit, inputLotId, triggerLot, divLotDropdown) {
        var container = document.createElement('div');
        container.className = 'custom-dropdown';

        triggerProduit.className = 'dropdown-trigger';
        triggerProduit.innerHTML = '<span>Choisir un produit</span> <i class="fa-solid fa-chevron-down"></i>';
        container.appendChild(triggerProduit);

        var ul = document.createElement('ul');
        ul.className = 'dropdown-menu';

        (produitsData || []).forEach(function (p) {
            var li = document.createElement('li');
            li.textContent = p.code;
            li.dataset.id = p.id;

            // Effet au survol d'une option produit
            li.addEventListener('mouseenter', function () {
                if (parseInt(p.id) === ID_POULE) {
                    divLotDropdown.classList.remove('lot-disabled');
                }
            });

            // Clic sur un produit
            li.addEventListener('click', function () {
                ul.querySelectorAll('li').forEach(function (el) { el.classList.remove('selected'); });
                li.classList.add('selected');
                
                inputProduitId.value = p.id;
                triggerProduit.querySelector('span').textContent = p.code;

                // Logique spécifique à l'ID_POULE
                if (parseInt(p.id) === ID_POULE) {
                    divLotDropdown.classList.remove('lot-disabled');
                } else {
                    // Reset du lot si ce n'est pas une poule
                    inputLotId.value = '';
                    triggerLot.querySelector('span').textContent = 'N/A (Pas de lot)';
                    divLotDropdown.classList.add('lot-disabled');
                    divLotDropdown.querySelectorAll('.dropdown-menu li').forEach(function (el) { el.classList.remove('selected'); });
                }
            });

            ul.appendChild(li);
        });

        // Si on quitte le bloc produit sans avoir cliqué, on réévalue l'état selon la valeur réelle
        container.addEventListener('mouseleave', function () {
            if (parseInt(inputProduitId.value) !== ID_POULE) {
                divLotDropdown.classList.add('lot-disabled');
            }
        });

        container.appendChild(ul);
        return container;
    }

    function creerDropdownLots(inputLotId, triggerLot) {
        var container = document.createElement('div');
        container.className = 'custom-dropdown lot-disabled'; // Bloqué par défaut tant qu'on a pas choisi Poule

        triggerLot.className = 'dropdown-trigger';
        triggerLot.innerHTML = '<span>Sélectionner lot</span> <i class="fa-solid fa-chevron-down"></i>';
        container.appendChild(triggerLot);

        var ul = document.createElement('ul');
        ul.className = 'dropdown-menu';

        (lotsData || []).forEach(function (l) {
            var li = document.createElement('li');
            li.textContent = 'Lot ' + l.id;
            li.dataset.id = l.id;

            li.addEventListener('click', function () {
                ul.querySelectorAll('li').forEach(function (el) { el.classList.remove('selected'); });
                li.classList.add('selected');
                
                inputLotId.value = l.id;
                triggerLot.querySelector('span').textContent = 'Lot ' + l.id;
            });

            ul.appendChild(li);
        });

        container.appendChild(ul);
        return container;
    }

    function creerLigne() {
        var idx = ligneIndex++;
        var tr = document.createElement('tr');

        // Id générés uniques pour les inputs (important pour la soumission de formulaires en tableaux)
        var inputProduitId = document.createElement('input');
        inputProduitId.type = 'hidden';
        inputProduitId.name = 'ventesLignes[' + idx + '].produitId';

        var inputLotId = document.createElement('input');
        inputLotId.type = 'hidden';
        inputLotId.name = 'ventesLignes[' + idx + '].lotId';

        var triggerProduit = document.createElement('div');
        var triggerLot = document.createElement('div');

        // --- Cellule Lot ---
        var tdLot = document.createElement('td');
        var dropdownLots = creerDropdownLots(inputLotId, triggerLot);
        tdLot.appendChild(dropdownLots);
        tdLot.appendChild(inputLotId);

        // --- Cellule Produit ---
        var tdProduit = document.createElement('td');
        var dropdownProduits = creerDropdownProduits(inputProduitId, triggerProduit, inputLotId, triggerLot, dropdownLots);
        tdProduit.appendChild(dropdownProduits);
        tdProduit.appendChild(inputProduitId);

        // --- Cellule Quantité ---
        var tdQte = document.createElement('td');
        var inputQte = document.createElement('input');
        inputQte.type = 'number';
        inputQte.name = 'ventesLignes[' + idx + '].quantite';
        inputQte.min = '1';
        inputQte.placeholder = 'Qté';
        inputQte.required = true;
        tdQte.appendChild(inputQte);

        // --- Cellule Prix unitaire ---
        var tdPrix = document.createElement('td');
        var inputPrix = document.createElement('input');
        inputPrix.type = 'number';
        inputPrix.name = 'ventesLignes[' + idx + '].prixUnitaire';
        inputPrix.min = '0';
        inputPrix.step = '0.01';
        inputPrix.placeholder = 'Prix';
        inputPrix.required = true;
        tdPrix.appendChild(inputPrix);

        // --- Cellule Supprimer ---
        var tdSuppr = document.createElement('td');
        var btnSuppr = document.createElement('button');
        btnSuppr.type = 'button';
        btnSuppr.className = 'btn-supprimer-ligne';
        btnSuppr.title = 'Supprimer cette ligne';
        btnSuppr.innerHTML = '<i class="fa-solid fa-trash"></i>';
        btnSuppr.addEventListener('click', function () {
            var tbody = document.getElementById('lignes-tbody');
            if (tbody && tbody.rows.length > 1) tbody.removeChild(tr);
        });
        tdSuppr.appendChild(btnSuppr);

        tr.appendChild(tdProduit);
        tr.appendChild(tdLot);
        tr.appendChild(tdQte);
        tr.appendChild(tdPrix);
        tr.appendChild(tdSuppr);

        return tr;
    }

    document.addEventListener('DOMContentLoaded', function () {
        var tbody = document.getElementById('lignes-tbody');
        var btn   = document.getElementById('btn-ajouter-ligne');
        if (!tbody || !btn) return;

        // Première ligne par défaut
        tbody.appendChild(creerLigne());

        btn.addEventListener('click', function () {
            tbody.appendChild(creerLigne());
        });
    });

})();