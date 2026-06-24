async function loadSelects(form) {

    const races = await fetch("/lots/data/races").then(r => r.json());
    const batiments = await fetch("/lots/data/batiments").then(r => r.json());
    const statuts = await fetch("/lots/data/statuts").then(r => r.json());

   console.log("batiment", batiments);
console.log("race", races);
console.log("statut", statuts);

    form.querySelector("#race").innerHTML =
        races.map(r => `<option value="${r.id}">${r.nom}</option>`).join("");

    form.querySelector("#batiment").innerHTML =
        batiments.map(b => `<option value="${b.id}">${b.nom}</option>`).join("");

    form.querySelector("#statut").innerHTML =
        statuts.map(s => `<option value="${s.code}">${s.code}</option>`).join("");
}
document.querySelectorAll(".btn-modifier").forEach(button => {

    button.addEventListener("click", async function () {

        const btn = this;

        const lot = {
            id: btn.dataset.id,
            race: btn.dataset.race,
            statut: btn.dataset.statut,
            batiment: btn.dataset.batiment,
            nombreInitial: btn.dataset.nombre
        };

        console.log("Modifier lot:", lot);

           const overlay = document.getElementById("reform-overlay");
        const container = document.getElementById("reform-container");

        overlay.classList.add("active");

        const form = document.createElement("form");
  
        form.classList.add("reform-form");
        form.method = "post";
        form.action = `/lots/modifier/${lot.id}`;

        form.innerHTML = `
            <h3>Modifier le lot ${lot.id}</h3>

            <div class="form-group">
                <label>Race :</label>
                <select name="race" id="race" required></select>
            </div>

            <div class="form-group">
                <label>Statut :</label>
                <select name="statut" id="statut" required></select>
            </div>

            <div class="form-group">
                <label>Bâtiment :</label>
                <select name="batiment" id="batiment" required></select>
            </div>

            <div class="form-group">
                <label>Nombre initial :</label>
                <input type="number" name="nombreInitial" id="nombreInitial" min="1" required>
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

       setTimeout(() => {
    form.querySelector("#race").value = String(lot.race);
    form.querySelector("#statut").value = String(lot.statut);
    form.querySelector("#batiment").value = String(lot.batiment);
    form.querySelector("#nombreInitial").value = lot.nombreInitial;
}, 0);

        // fermer
        const closeModal = () => {
            overlay.classList.remove("active");
            container.innerHTML = "";
        };

        form.querySelector(".close-modal").addEventListener("click", closeModal);

        overlay.addEventListener("click", function (e) {
            if (e.target === overlay) closeModal();
        });

        // submit
   form.addEventListener("submit", function (e) {
    e.preventDefault();

    const btnSubmit = form.querySelector(".btn-valider");
    btnSubmit.disabled = true;

    const confirmation = confirm(
        `Confirmer la modification du lot ${lot.id} ?`
    );

    if (!confirmation) {
        btnSubmit.disabled = false;
        return;
    }

    btnSubmit.textContent = "Modification en cours...";

    form.submit();
});
       
      form.querySelector(".btn-supprimer").addEventListener("click", function () {

    console.log("click delete OK");

    if (confirm(`Supprimer le lot ${lot.id} ?`)) {
        window.location.href = `/lots/${lot.id}/supprimer`;
    }
});

    });

});