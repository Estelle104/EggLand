document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("lotForm");

    if (!form) return;

    const race = document.querySelector("[name='race']");
    const dateArrivee = document.querySelector("[name='dateArrivee']");
    const nombreInitial = document.querySelector("[name='nombreInitial']");
    const batiment = document.querySelector("[name='batiment']");

    function setErreur(champ) {
        champ.classList.remove("valid");
        champ.classList.add("invalid");
    }

    function setValide(champ) {
        champ.classList.remove("invalid");
        champ.classList.add("valid");
    }

    function verifierRace() {
        race.value ? setValide(race) : setErreur(race);
    }

    function verifierBatiment() {
        batiment.value ? setValide(batiment) : setErreur(batiment);
    }

    function verifierNombre() {
        parseInt(nombreInitial.value) > 0
            ? setValide(nombreInitial)
            : setErreur(nombreInitial);
    }

    function verifierDate() {
        if (!dateArrivee.value) {
            setErreur(dateArrivee);
            return;
        }

        const dateChoisie = new Date(dateArrivee.value);
        const aujourdHui = new Date();

        aujourdHui.setHours(0, 0, 0, 0);

        if (dateChoisie > aujourdHui) {
            setErreur(dateArrivee);
        } else {
            setValide(dateArrivee);
        }
    }

    race.addEventListener("change", verifierRace);
    batiment.addEventListener("change", verifierBatiment);
    nombreInitial.addEventListener("input", verifierNombre);
    dateArrivee.addEventListener("change", verifierDate);

    form.addEventListener("submit", function (e) {

        verifierRace();
        verifierBatiment();
        verifierNombre();
        verifierDate();

        const erreurs = form.querySelectorAll(".invalid");

        if (erreurs.length > 0) {
            e.preventDefault();
        }
    });

});