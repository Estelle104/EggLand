# TODO - Module Gestion des Lots

## Backend

### T2.3 - LotService

**Durée estimée : 1h30**

* [x] Implémenter la création d'un lot
* [x] Vérifier la capacité disponible du bâtiment avant création
* [x] Calculer l'âge actuel du lot (≈ 5 mois au démarrage)
* [x] Ajouter les validations métier nécessaires
* [x] Tester les cas d'erreur (bâtiment plein, données invalides)

**Fichiers :**

* `LotService.java`
* `LotRepository.java`
* `BatimentRepository.java`

---

### T2.4 - LotController

**Durée estimée : 1h**

* [x] Créer les endpoints CRUD du lot
* [ ] Ajouter le filtrage par statut
* [ ] Ajouter le filtrage par bâtiment
* [ ] Vérifier les redirections et messages utilisateur

**Fichiers :**

* `LotController.java`
* `LotService.java`

---

### T2.5 - Action Réforme

**Durée estimée : 45 min**

* [ ] Ajouter l'action "Réformer un lot"
* [ ] Mettre à jour le statut vers `REFORME`
* [ ] Enregistrer `date_reforme`
* [ ] Rediriger vers le module de vente après réforme
* [ ] Vérifier la cohérence des données

**Fichiers :**

* `LotService.java`
* `LotController.java`
* `model/Lot.java`

---

## Frontend

### T2.6 - Liste et Formulaire des Lots

**Durée estimée : 1h**

* [ ] Créer la page `lots/liste.html`
* [ ] Afficher les lots sous forme de tableau
* [ ] Ajouter les filtres disponibles
* [x] Créer la page `lots/form.html`
* [ ] Ajouter les validations du formulaire
* [ ] Vérifier l'affichage responsive

**Fichiers :**

* `templates/lots/liste.html`
* `templates/lots/form.html`

---

### T2.7 - Détail d'un Lot

**Durée estimée : 2h**

* [ ] Créer la page `lots/detail.html`
* [ ] Mettre en place des onglets Bootstrap
* [ ] Onglet Production
* [ ] Onglet Mortalité
* [ ] Onglet Traitements
* [ ] Prévoir l'extension vers d'autres historiques
* [ ] Ajouter le bouton "Réformer"
* [ ] Ajouter une confirmation avant réforme
* [ ] Tester la navigation entre les onglets

**Fichier :**

* `templates/lots/detail.html`

---

## Estimation Totale

| Tâche     | Durée    |
| --------- | -------- |
| T2.3      | 1h30     |
| T2.4      | 1h00     |
| T2.5      | 0h45     |
| T2.6      | 1h00     |
| T2.7      | 2h00     |
| **Total** | **6h15** |
