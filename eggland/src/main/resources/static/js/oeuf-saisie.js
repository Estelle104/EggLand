const listeStatuts = window.listeStatutsOeufs || [];

        function ajouterLigne() {
            const container = document.getElementById('container-anomalies');
            const index = container.getElementsByClassName('anomalie-row').length;

            const div = document.createElement('div');
            div.className = 'anomalie-row';

            let optionsHtml = '<option value="">-- Aucune anomalie --</option>';
            if (listeStatuts) {
                listeStatuts.forEach(st => {
                    if (st.code !== 'valide' && st.code !== 'vendu' ) {
                        optionsHtml += `<option value="${st.id}">${st.code}</option>`;
                    }
                });
            }

            div.innerHTML = `
                <div class="form-group group-statut">
                    <label class="small">Type d'anomalie</label>
                    <select name="oeufStatuts[${index}].statut.id">
                        ${optionsHtml}
                    </select>
                </div>
                <div class="form-group group-quantite">
                    <label class="small">Quantité impactée</label>
                    <input type="number" name="oeufStatuts[${index}].quantite" placeholder="0" min="1" />
                </div>
                <button type="button" class="btn-supprimer" onclick="supprimerLigne(this)">
                    <i class="fa-solid fa-trash-can"></i>
                </button>
            `;
            container.appendChild(div);
        }

        function supprimerLigne(button) {
            const row = button.closest('.anomalie-row');
            row.remove();
            
            const rows = document.getElementById('container-anomalies').getElementsByClassName('anomalie-row');
            Array.from(rows).forEach((currentRow, newIndex) => {
                const select = currentRow.querySelector('.group-statut select');
                const input = currentRow.querySelector('.group-quantite input');
                if (select) select.name = `oeufStatuts[${newIndex}].statut.id`;
                if (input) input.name = `oeufStatuts[${newIndex}].quantite`;
            });
        }
