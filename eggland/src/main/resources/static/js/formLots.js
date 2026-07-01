document.addEventListener("DOMContentLoaded", () => {

    const container = document.getElementById("lotsContainer");
    const btnAdd = document.getElementById("btnAdd");

    ajouterSuppression(container.querySelector(".lot-item"));

    btnAdd.addEventListener("click", () => {

        const clone = container.firstElementChild.cloneNode(true);

        clone.querySelector(".race-select").selectedIndex = 0;
        clone.querySelector(".nombre-input").value = "";

        ajouterSuppression(clone);

        container.appendChild(clone);

    });

    function ajouterSuppression(bloc){

        bloc.querySelector(".btn-remove").addEventListener("click", () => {

            if(container.children.length == 1){
                alert("Au moins une ligne est obligatoire.");
                return;
            }

            bloc.remove();

        });

    }

});