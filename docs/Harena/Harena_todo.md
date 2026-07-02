## Harena | Race, Bâtiment & Stock nourriture (Sakafo)

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T2.1 | Race CRUD : Controller + Service + Repository + templates liste + form | `RaceController.java`, `RaceService.java`, `RaceRepository.java`, `templates/races/liste.html`, `templates/races/form.html` | 1h |
| T2.2 | Bâtiment CRUD + affichage capacité restante (badge coloré) | `BatimentController.java`, `BatimentService.java`, `BatimentRepository.java`, `templates/batiments/liste.html`, `templates/batiments/form.html` | 1h |
| T5.1 | CRUD `Sakafo` + `MvtStockService` : calcul stock actuel = entrées - sorties | `SakafoController.java`, `SakafoService.java`, `MvtStockService.java`, `SakafoRepository.java` | 1h |
| T5.2 | Si stock ≤ seuil → `NotificationService.creer(STOCK_FAIBLE)` | `MvtStockService.java`, `NotificationService.java` | 1h |
| T5.3 | Achat → créer `MvtArgent(sortie, achat_sakafo)` automatiquement | `MvtStockService.java`, `MvtArgentService.java` | 1h |
| T5.4 | Templates : `stock/liste.html` (progress bar colorée) + formulaires entrée/sortie + historique filtrable | `templates/stock/liste.html`, `templates/stock/entree.html`, `templates/stock/sortie.html`, `templates/stock/historique.html` | 2h |

**Total : 7h — 2,5 jours (3h/jour)**

---

### Fait:

**T2.1 — Race CRUD**
- Liste des races avec actions Modifier / Supprimer
- Formulaire de création et d'édition (nom, prix unitaire, rendement/mois)
- Navigation dans le sidebar

**T2.2 — Bâtiment CRUD**
- Liste des bâtiments avec badge coloré sur la capacité (vert ≥ 1000, orange ≥ 500, rouge < 500)
- Formulaire de création et d'édition (nom, capacité)

**T5.1 — Nourriture (Sakafo) + Stock**
- CRUD complet Nourriture (libellé, prix unitaire, seuil d'alerte)
- `MvtStockService` avec calcul du stock actuel = entrées − sorties (via `TypeMvt`)
- Récupération des types "entree" / "sortie" depuis `TypeMvtRepository.findByCode()`

**T5.2 — Alerte stock faible**
- Vérification automatique après chaque mouvement de stock : si stock ≤ `seuilAlerte` de la nourriture → `NotificationService.creer("STOCK_FAIBLE")`

**T5.3 — Achat → MvtArgent automatique**
- Quand un mouvement de type "entree" est créé, `MvtArgentService.creerSortie()` génère automatiquement une sortie d'argent avec `montant = quantité × prixUnitaire` et `catégorie = "achat_nourriture"`

**T5.4 — Templates stock**
- `stock/liste.html` : grille des nourritures avec **barre de progression colorée** (vert ≥ seuil, orange ≥ 50%, rouge < 50%) + badge STOCK OK / STOCK FAIBLE
- `stock/entree.html` : formulaire entrée (select nourriture, quantité, date)
- `stock/sortie.html` : formulaire sortie (select nourriture, quantité, date)
- `stock/historique.html` : tableau avec filtres (nourriture, type entrée/sortie, dates début/fin)

**Sécurité & navigation**
- Routes `/races`, `/batiments`, `/stock`, `/nourritures` accessibles à admin + gestionnaire
- Liens Races, Bâtiments, Stock, Nourritures ajoutés dans le sidebar

**Autre ajout:**
- Creation de controller pour `MvtArgent`
- `mvtargent/liste.html`: Affichage des liste de Mouvement d'argent (date, type, montant, categorie)
- modification(amelioration) de header & sidebar
- ajout de pagination pour les listes 5 par 5 chaque pages
- support guide d utilisation + fanazava code oe ty manao inona
- Systeme de recherche globale :
  - `RechercheService` : recherche SQL dynamique dans toutes les colonnes textes/nombres via `information_schema`
  - `RechercheController` : endpoint `GET /recherche`
  - `recherche/resultats.html` : template avec resultats groupes par table, lien "Voir" vers la page correspondante
  - Barre de recherche dans le header (action vers `/recherche`)
  - Filtrage des liens "Voir" selon le rôle : admin voit tout, gestionnaire voit seulement les pages accessibles
  - Gestion des tables sans controleur dedie (pas de bouton "Voir" pour `notification`, `configuration`, `traitement`)
- Correction du `line-height` sur `.sidebar` et `.header` pour eviter les conflits Bootstrap (dashboard/finance/exports)

**Remarque:**
- 
