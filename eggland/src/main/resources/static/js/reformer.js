document.querySelectorAll(".btn-reformer").forEach(button => {
console.log(document.querySelectorAll(".btn-reformer").length);
    button.addEventListener("click", function () {

        const lotId = this.dataset.id;

        const overlay = document.getElementById("reform-overlay");
        const container = document.getElementById("reform-container");

        if (!overlay || !container) {
            console.error("Overlay ou container introuvable");
            return;
        }

        overlay.classList.add("active");

        const form = document.createElement("form");

        form.classList.add("reform-form");
        form.method = "post";
        form.action = `/admin/lots/reforme/${lotId}`;

        form.innerHTML = `
            <h3>Réformer le lot ${lotId}</h3>

            <label for="dateReforme">
                Date de réforme
            </label>

            <input
                type="date"
                id="dateReforme"
                name="dateReform"
                required>

            <div class="modal-buttons">

                <button type="submit" class="btn-danger">
                    Valider
                </button>

                <button type="button" class="btn-secondary close-modal">
                    Annuler
                </button>

            </div>
        `;

        container.innerHTML = "";
        container.appendChild(form);

        const closeModal = () => {
            overlay.classList.remove("active");
            container.innerHTML = "";
        };

        form.querySelector(".close-modal")
            .addEventListener("click", closeModal);

        overlay.onclick = function (e) {
            if (e.target === overlay) {
                closeModal();
            }
        };

        form.addEventListener("submit", function (e) {

            const dateInput = form.querySelector("#dateReforme");

            if (!dateInput.value) {
                e.preventDefault();
                alert("Veuillez sélectionner une date");
                return;
            }

            const selectedDate = new Date(dateInput.value);

            const today = new Date();
            today.setHours(0, 0, 0, 0);

            if (selectedDate > today) {
                e.preventDefault();
                alert("La date ne peut pas être dans le futur");
                return;
            }

            const confirmation = confirm(
                `Confirmer la réforme du lot ${lotId} ?`
            );

            if (!confirmation) {
                e.preventDefault();
                return;
            }

            const submitBtn =
                form.querySelector(".btn-danger");

            submitBtn.disabled = true;
            submitBtn.textContent = "Envoi...";
        });

    });

});