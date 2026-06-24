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

* [] Créer les endpoints CRUD du lot
* [x] Ajouter le filtrage par statut
* [x] Ajouter le filtrage par bâtiment
* [x] Vérifier les redirections et messages utilisateur

**Fichiers :**

* `LotController.java`
* `LotService.java`

---

### T2.5 - Action Réforme

**Durée estimée : 45 min**

* [x] Ajouter l'action "Réformer un lot"
* [x] Mettre à jour le statut vers `REFORME`
* [x] Enregistrer `date_reforme`
* [x] Rediriger vers le module de vente après réforme
* [x] Vérifier la cohérence des données

**Fichiers :**

* `LotService.java`
* `LotController.java`
* `model/Lot.java`

---

## Frontend

### T2.6 - Liste et Formulaire des Lots

**Durée estimée : 1h**

* [x] Créer la page `lots/liste.html`
* [x] Afficher les lots sous forme de tableau
* [x] Ajouter les filtres disponibles
* [x] Créer la page `lots/form.html`
* [x] Ajouter les validations du formulaire
* [x] Vérifier l'affichage responsive

**Fichiers :**

* `templates/lots/liste.html`
* `templates/lots/form.html`

---

### T2.7 - Détail d'un Lot

**Durée estimée : 2h**

* [x] Créer la page `lots/detail.html`
* [x] Mettre en place des onglets Bootstrap
* [x] Onglet Production
* [x] Onglet Mortalité
* [x] Onglet Traitements
  * [x] Prévoir l'extension vers d'autres historiques
 * [x] Ajouter le bouton "Réformer"
* [x] Ajouter une confirmation avant réforme
* [x] Tester la navigation entre les onglets

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
