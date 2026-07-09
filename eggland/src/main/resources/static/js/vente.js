(function () {
    'use strict';

    var ID_POULE = 2;
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

    // Récupère la liste des races disponibles (avec quantité et prix) pour un lot donné
    function getRacesDuLot(lotId) {
        var lot = (lotsData || []).find(function (l) { return String(l.id) === String(lotId); });
        if (!lot || !lot.lotRaces) return [];
        return lot.lotRaces;
    }

    function creerDropdownRaces(inputRaceId, triggerRace, inputPrix) {
        var container = document.createElement('div');
        container.className = 'custom-dropdown race-disabled'; // désactivé tant qu'aucun lot n'est choisi

        triggerRace.className = 'dropdown-trigger';
        triggerRace.innerHTML = '<span>Sélectionner une race</span><i class="fa-solid fa-chevron-down"></i>';
        container.appendChild(triggerRace);

        var ul = document.createElement('ul');
        ul.className = 'dropdown-menu';
        container.appendChild(ul);

        // Reconstruit la liste des races à partir du lot sélectionné
        container.rafraichir = function (lotId) {
            ul.innerHTML = '';
            inputRaceId.value = '';
            triggerRace.querySelector('span').textContent = 'Sélectionner une race';
            inputPrix.value = '';

            var racesDuLot = getRacesDuLot(lotId);
            if (!lotId || racesDuLot.length === 0) {
                container.classList.add('race-disabled');
                return;
            }
            container.classList.remove('race-disabled');

            racesDuLot.forEach(function (lr) {
                if (!lr.race) return;
                var li = document.createElement('li');
                var label = lr.race.nom + ' (restant : ' + (lr.nombre != null ? lr.nombre : 0) + ')';
                li.textContent = label;
                li.dataset.id = lr.race.id;

                li.addEventListener('click', function () {
                    ul.querySelectorAll('li').forEach(function (el) { el.classList.remove('selected'); });
                    li.classList.add('selected');
                    inputRaceId.value = lr.race.id;
                    triggerRace.querySelector('span').textContent = label;

                    // Prix unitaire rempli automatiquement selon la race choisie
                    if (lr.race.prixUnitaire != null) {
                        inputPrix.value = lr.race.prixUnitaire;
                        calculerTotalVente();
                    }
                });

                ul.appendChild(li);
            });
        };

        return container;
    }

    function creerDropdownLots(inputLotId, triggerLot, onLotChange) {
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
                if (onLotChange) onLotChange(l.id);
            });

            ul.appendChild(li);
        });

        container.appendChild(ul);
        return container;
    }

    function creerDropdownProduits(inputProduitId, triggerProduit, inputLotId, triggerLot,
                                    divLotDropdown, divRaceDropdown) {
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

                    // Le produit n'est plus "poule" : on masque/réinitialise le dropdown de race
                    if (divRaceDropdown && divRaceDropdown.rafraichir) {
                        divRaceDropdown.rafraichir(null);
                    }
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

        var inputRaceId = document.createElement('input');
        inputRaceId.type = 'hidden';
        inputRaceId.name = 'raceId';

        var triggerProduit = document.createElement('div');
        var triggerLot     = document.createElement('div');
        var triggerRace    = document.createElement('div');

        var tdProduit = document.createElement('td');

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

        var tdLot = document.createElement('td');
        var tdRace = document.createElement('td');

        var dropdownRaces = creerDropdownRaces(inputRaceId, triggerRace, inputPrix);
        tdRace.appendChild(dropdownRaces);
        tdRace.appendChild(inputRaceId);

        var dropdownLots = creerDropdownLots(inputLotId, triggerLot, function (lotId) {
            dropdownRaces.rafraichir(lotId);
        });
        tdLot.appendChild(dropdownLots);
        tdLot.appendChild(inputLotId);

        var dropdownProduits = creerDropdownProduits(
            inputProduitId, triggerProduit, inputLotId, triggerLot, dropdownLots, dropdownRaces);
        tdProduit.appendChild(dropdownProduits);
        tdProduit.appendChild(inputProduitId);

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
        tr.appendChild(tdRace);
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
