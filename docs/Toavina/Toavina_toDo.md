# TODO - Module Finance & Export

## Backend

### T9.1 - FinanceService

**Durée estimée : 2h30**

* [x] Créer FinanceService avec calculs financiers
* [x] Implémenter getResumeFinancier()
* [x] Implémenter getRecettesParCategorie()
* [x] Implémenter getDepensesParCategorie()
* [x] Implémenter getCoutRevientParLot()
* [x] Implémenter getRecettesMensuelles12Mois()
* [x] Implémenter getDepensesMensuelles12Mois()
* [x] Ajouter requêtes natives dans MvtArgentRepository
* [x] Ajouter champ `lot` dans MvtArgent (ManyToOne)
* [x] Tester les calculs financiers

**Fichiers :**

* `service/FinanceService.java`
* `repository/MvtArgentRepository.java`
* `model/MvtArgent.java`

---

### T9.2 - Export PDF (iText 7)

**Durée estimée : 1h30**

* [x] Créer PdfExportService
* [x] Implémenter generateBonLivraisonPdf()
* [x] Implémenter generatePaiementSalairePdf()
* [x] Implémenter generateRapportFinancePdf() (stub)
* [x] Ajouter dépendance iText 7 Core dans pom.xml
* [x] Tester les exports PDF

**Fichiers :**

* `service/PdfExportService.java`
* `pom.xml`

---

### T9.3 - Export Excel (Apache POI)

**Durée estimée : 2h30**

* [x] Créer ExcelExportService
* [x] Implémenter exportProductionOeufs()
* [x] Implémenter exportMouvementStock()
* [x] Implémenter exportPaiementsSalaires()
* [x] Implémenter importListeVentes()
* [x] Créer classe interne ImportResult
* [x] Ajouter méthodes dans ClientRepository et ProduitVenteRepository
* [x] Ajouter dépendance Apache POI dans pom.xml
* [x] Tester exports et import Excel

**Fichiers :**

* `service/ExcelExportService.java`
* `repository/ClientRepository.java`
* `repository/ProduitVenteRepository.java`
* `pom.xml`

---

### Correctifs & Optimisations

**Durée estimée : 3h**

* [x] Corriger colonne `id_statut` inexistante dans Lot (reset_db.sql)
* [x] Aligner `DetailVente.produit` vs `id_produit` (reset_db.sql)
* [x] Ajouter `client_id` manquant dans DetailVente (reset_db.sql)
* [x] Corriger collisions de séquence après réinsertion (insert_data.sql)
* [x] Remplacer DELETE par UPDATE pour table `lot` (reset_db.sql)
* [x] Autoriser `/finance/**` dans SecurityConfig
* [x] Corriger bouton suppression livraison (th:onsubmit → onsubmit)
* [x] Ajouter endpoint manquant export PDF livraison
* [x] Corriger `race.code` → `race.nom` dans template finance
* [x] Corriger parsing `YearMonth` pour input month
* [x] Remplacer `#dates.createNow().minusDays()` par attributs modèle
* [x] Corriger syntaxe `th:replace` dépréciée (ajouter `~{}`)

**Fichiers :**

* `scripts/reset_db.sql`
* `scripts/insert_data.sql`
* `config/SecurityConfig.java`
- `templates/livraisons/liste.html`
* `controller/LivraisonController.java`
* `templates/finance/index.html`
* `controller/FinanceController.java`
* `controller/LivraisonController.java`
* `controller/MvtStockController.java`
* `controller/OeufController.java`
* `templates/fragments/sidebar.html`
* Tous les templates (syntax th:replace)

---

## Frontend

### T9.4 - Interface Finance (Thymeleaf + Chart.js)

**Durée estimée : 2h**

* [x] Créer `templates/finance/index.html`
* [x] 4 cartes résumé (Recettes, Dépenses, Bénéfice, Marge)
* [x] Graphique évolution 12 mois (Chart.js barres)
* [x] Tableau répartition par catégorie (Recettes/Dépenses)
* [x] Tableau coût de revient par lot
* [x] Formulaires export PDF (Bon livraison, Paiement salaires)
* [x] Formulaires export Excel (3 formulaires)
* [x] Formulaire import Excel ventes
* [x] Tester l'interface complète

**Fichier :**

* `templates/finance/index.html`

---

### Page centralisée Import/Export

**Durée estimée : 1h**

* [x] Créer `templates/exports/index.html`
* [x] Ajouter endpoint `/exports` dans FinanceController
* [x] Ajouter lien "Export / Import" dans sidebar
* [x] 3 cartes : Export PDF, Export Excel, Import Excel
* [x] Liens rapides vers pages liées

**Fichiers :**

* `templates/exports/index.html`
* `controller/FinanceController.java`
* `templates/fragments/sidebar.html`

---

### Boutons d'export sur pages entités

**Durée estimée : 30min**

* [x] `templates/livraisons/liste.html` - Export PDF Bon Livraison
* [x] `templates/stock/liste.html` - Export XLSX Mouvement Stock
* [x] `templates/oeufs/historique.html` - Export XLSX Production Œufs
* [x] `templates/employes/recap.html` - Export PDF + XLSX Paiement Salaires

**Fichiers :**

* `templates/livraisons/liste.html`
* `templates/stock/liste.html`
* `templates/oeufs/historique.html`
* `templates/employes/recap.html`


## Estimation Totale

| Tâche | Durée |
| -------- | -------- |
| T9.1 - FinanceService | 2h30 |
| T9.2 - Export PDF | 1h30 |
| T9.3 - Export Excel | 2h30 |
| T9.4 - Interface Finance | 2h00 |
| Correctifs & Optimisations | 3h00 |
| Page centralisée Import/Export | 1h00 |
| Boutons pages entités | 0h30 |
| **Total** | **~15h** |