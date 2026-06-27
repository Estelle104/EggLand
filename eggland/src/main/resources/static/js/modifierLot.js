async function loadSelects(form) {
    const races = await fetch("/lots/data/races").then(r => r.json());
    const batiments = await fetch("/lots/data/batiments").then(r => r.json());

    console.log("batiment", batiments);
    console.log("race", races);

    window.racesData = races; // Stockez les races globalement
    window.batimentsData = batiments;
}

function createRaceRow(race = null, nombre = null) {
    const racesHTML = window.racesData.map(r => 
        `<option value="${r.id}" ${race && race.id == r.id ? 'selected' : ''}>${r.nom}</option>`
    ).join("");
    
    return `
        <div class="race-row" style="margin-bottom: 15px; padding: 10px; border: 1px solid #ddd; border-radius: 5px;">
            <div class="form-group">
                <label>Race :</label>
                <select name="listeRace" required style="width: 100%; padding: 8px;">
                    <option value="">-- Sélectionnez une race --</option>
                    ${racesHTML}
                </select>
            </div>

            <div class="form-group">
                <label>Nombre :</label>
                <input type="number" name="nbrPoule" value="${nombre || ''}" min="1" required style="width: 100%; padding: 8px;">
            </div>

            <button type="button" class="btn-remove-race" style="margin-top: 10px;">
                <i class="fa-solid fa-minus"></i> Supprimer cette race
            </button>
        </div>
    `;
}

document.querySelectorAll(".btn-modifier").forEach(button => {

    button.addEventListener("click", async function () {

        const btn = this;
        const lotId = btn.dataset.id;

        // Récupérer les données du lot avec ses races
        const lot = await fetch(`/lots/api/${lotId}`).then(r => r.json());

        console.log("Lot avec races:", lot);

        const overlay = document.getElementById("reform-overlay");
        const container = document.getElementById("reform-container");

        overlay.classList.add("active");

        const form = document.createElement("form");
        form.classList.add("reform-form");
        form.method = "post";
        form.action = `/lots/modifier/${lot.id}`;

        const batimentsHTML = window.batimentsData.map(b => 
            `<option value="${b.id}" ${lot.batiment.id == b.id ? 'selected' : ''}>${b.nom}</option>`
        ).join("");

        form.innerHTML = `
            <h3>Modifier le lot ${lot.id}</h3>

            <div id="races-container">
                ${lot.lotRaces.map(lr => createRaceRow(lr.race, lr.nombre)).join('')}
            </div>

            <button type="button" id="btn-add-race" class="btn btn-success" style="margin: 10px 0;">
                <i class="fa-solid fa-plus"></i> Ajouter une race
            </button>

            <div class="form-group">
                <label>Bâtiment :</label>
                <select name="batiment" id="batiment" required style="width: 100%; padding: 8px;">
                    <option value="">-- Sélectionnez un bâtiment --</option>
                    ${batimentsHTML}
                </select>
            </div>

            <div class="modal-buttons">
                <button type="submit" class="btn btn-primary btn-valider">Valider</button>
                <button type="button" class="btn btn-danger btn-supprimer">Supprimer</button>
                <button type="button" class="btn btn-secondary close-modal">Annuler</button>
            </div>
        `;

        container.innerHTML = "";
        container.appendChild(form);

        await loadSelects(form);

        // Ajouter une race dynamiquement
        form.querySelector("#btn-add-race").addEventListener("click", function() {
            const container = form.querySelector("#races-container");
            const newRow = document.createElement("div");
            newRow.innerHTML = createRaceRow();
            container.appendChild(newRow);
            attachRemoveRaceListener(newRow);
        });

        // Attacher les listeners pour supprimer une race
        form.querySelectorAll(".race-row").forEach(row => {
            attachRemoveRaceListener(row);
        });

        function attachRemoveRaceListener(row) {
            row.querySelector(".btn-remove-race").addEventListener("click", function() {
                if (form.querySelectorAll(".race-row").length > 1) {
                    row.remove();
                } else {
                    alert("Au moins une race est obligatoire");
                }
            });
        }

        // Fermer
        const closeModal = () => {
            overlay.classList.remove("active");
            container.innerHTML = "";
        };

        form.querySelector(".close-modal").addEventListener("click", closeModal);

        overlay.addEventListener("click", function (e) {
            if (e.target === overlay) closeModal();
        });

        // Submit
        form.addEventListener("submit", function (e) {
            e.preventDefault();

            const btnSubmit = form.querySelector(".btn-valider");
            btnSubmit.disabled = true;

            const confirmation = confirm(`Confirmer la modification du lot ${lot.id} ?`);

            if (!confirmation) {
                btnSubmit.disabled = false;
                return;
            }

            btnSubmit.textContent = "Modification en cours...";
            form.submit();
        });

        // Supprimer
        form.querySelector(".btn-supprimer").addEventListener("click", function () {
            if (confirm(`Supprimer le lot ${lot.id} ?`)) {
                window.location.href = `/lots/supprimer/${lot.id}`;
            }
        });

    });

});