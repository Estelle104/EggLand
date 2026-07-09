(function () {
    'use strict';

    var ID_POULE = 3;
    var ligneIndex = 0;

    function calculerTotalVente() {
        var tbody = document.getElementById('lignes-tbody');
        var totalGlobal = 0;
        if (!tbody) return;
        tbody.querySelectorAll('tr').forEach(function (tr) {
            var qte  = parseFloat((tr.querySelector('input[name="quantite"]')    || {}).value) || 0;
            var prix = parseFloat((tr.querySelector('input[name="prixUnitaire"]') || {}).value) || 0;
            totalGlobal += qte * prix;
        });
        var el = document.getElementById('total-vente');
        if (el) el.textContent = totalGlobal.toFixed(2) + ' Ar';
    }

      function creerDropdownLots(inputLotId, triggerLot) {
        var container = document.createElement('div');
        container.className = 'custom-dropdown lot-disabled'; // désactivé par défaut

        triggerLot.className = 'dropdown-trigger';
        triggerLot.innerHTML = '<span>Sélectionner un lot</span><i class="fa-solid fa-chevron-down"></i>';
        container.appendChild(triggerLot);

        var ul = document.createElement('ul');
        ul.className = 'dropdown-menu';

        (lotsData || []).forEach(function (l) {
            var li = document.createElement('li');
            var label = 'Lot ' + l.id;
            if (l.statut && l.statut.code) label += ' (' + l.statut.code + ')';
            li.textContent = label;
            li.dataset.id = l.id;

            li.addEventListener('click', function () {
                ul.querySelectorAll('li').forEach(function (el) { el.classList.remove('selected'); });
                li.classList.add('selected');
                inputLotId.value = l.id;
                triggerLot.querySelector('span').textContent = label;
            });

            ul.appendChild(li);
        });

        container.appendChild(ul);
        return container;
    }

    function creerDropdownProduits(inputProduitId, triggerProduit, inputLotId, triggerLot, divLotDropdown) {
        var container = document.createElement('div');
        container.className = 'custom-dropdown';

        triggerProduit.className = 'dropdown-trigger';
        triggerProduit.innerHTML = '<span>Choisir un produit</span><i class="fa-solid fa-chevron-down"></i>';
        container.appendChild(triggerProduit);

        var ul = document.createElement('ul');
        ul.className = 'dropdown-menu';

        (produitsData || []).forEach(function (p) {
            var li = document.createElement('li');
            li.textContent = p.code;
            li.dataset.id = p.id;

            li.addEventListener('click', function () {
                ul.querySelectorAll('li').forEach(function (el) { el.classList.remove('selected'); });
                li.classList.add('selected');
                inputProduitId.value = p.id;
                triggerProduit.querySelector('span').textContent = p.code;

                if (parseInt(p.id) === ID_POULE) {
                    divLotDropdown.classList.remove('lot-disabled');
                } else {
                    inputLotId.value = '';
                    triggerLot.querySelector('span').textContent = 'Sélectionner un lot';
                    divLotDropdown.querySelectorAll('.dropdown-menu li')
                        .forEach(function (el) { el.classList.remove('selected'); });
                    divLotDropdown.classList.add('lot-disabled');
                }
            });

            ul.appendChild(li);
        });

        container.appendChild(ul);
        return container;
    }

  
    function creerLigne() {
        ligneIndex++;
        var tr = document.createElement('tr');

        var inputProduitId = document.createElement('input');
        inputProduitId.type = 'hidden';
        inputProduitId.name = 'produitId';

        var inputLotId = document.createElement('input');
        inputLotId.type = 'hidden';
        inputLotId.name = 'lotId';

        var triggerProduit = document.createElement('div');
        var triggerLot     = document.createElement('div');

        var tdLot = document.createElement('td');
        var dropdownLots = creerDropdownLots(inputLotId, triggerLot);
        tdLot.appendChild(dropdownLots);
        tdLot.appendChild(inputLotId);

        var tdProduit = document.createElement('td');
        var dropdownProduits = creerDropdownProduits(
            inputProduitId, triggerProduit, inputLotId, triggerLot, dropdownLots);
        tdProduit.appendChild(dropdownProduits);
        tdProduit.appendChild(inputProduitId);

        var tdQte = document.createElement('td');
        var inputQte = document.createElement('input');
        inputQte.type = 'number';
        inputQte.name = 'quantite';
        inputQte.min  = '1';
        inputQte.placeholder = 'Qté';
        inputQte.required = true;
        inputQte.addEventListener('input', calculerTotalVente);
        tdQte.appendChild(inputQte);

        var tdPrix = document.createElement('td');
        var inputPrix = document.createElement('input');
        inputPrix.type = 'number';
        inputPrix.name = 'prixUnitaire';
        inputPrix.min  = '0';
        inputPrix.step = '0.01';
        inputPrix.placeholder = 'Prix';
        inputPrix.required = true;
        inputPrix.addEventListener('input', calculerTotalVente);
        tdPrix.appendChild(inputPrix);

        var tdSuppr = document.createElement('td');
        var btnSuppr = document.createElement('button');
        btnSuppr.type = 'button';
        btnSuppr.className = 'btn-supprimer-ligne';
        btnSuppr.title = 'Supprimer';
        btnSuppr.innerHTML = '<i class="fa-solid fa-trash"></i>';
        btnSuppr.addEventListener('click', function () {
            var tbody = document.getElementById('lignes-tbody');
            if (tbody && tbody.rows.length > 1) {
                tbody.removeChild(tr);
                calculerTotalVente();
            }
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
        tbody.appendChild(creerLigne());
        btn.addEventListener('click', function () {
            tbody.appendChild(creerLigne());
            calculerTotalVente();
        });
    });

})();
