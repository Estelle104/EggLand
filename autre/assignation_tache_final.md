# Assignation tache — EggLand SI

> Spring Boot + Thymeleaf + Bootstrap 5 · 11 membres · 7 semaines

---

## Chef de projet: Estelle | Architecture + Setup général

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T0.1 | Init projet Spring Boot (Thymeleaf, Security, JPA, MySQL, Lombok) | `pom.xml`, `application.properties` | 1h |
| T0.2 | Toutes les entités `@Entity` JPA + `application.properties` | `model/*.java`, `application.properties` | 2h |
| T0.3 | Layout Thymeleaf (`layout.html`) : navbar Bootstrap + `sec:authorize` + sidebar | `templates/layout.html` | 2,5h |
| T0.4 | Structure packages + conventions équipe + Git (branches, PRs) | `.gitignore`, `README.md` | 2h |
| T0.5 | Revues de code hebdomadaires + support équipe + déploiement final | Tous les fichiers du projet | 6h |

**Total : 13,5h — 5 jours (3h/jour)**

---

## Toky | Auth & Sécurité

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T1.1 | `SecurityConfig` : formLogin, routes protégées, rôles ADMIN/GESTIONNAIRE | `config/SecurityConfig.java` | 1h |
| T1.2 | `UserDetailsServiceImpl` + BCrypt (optionnel) + gestion sessions | `service/UserDetailsServiceImpl.java`, `config/SecurityConfig.java` | 1,5h |
| T1.3 (Optionnel) | CRUD UserAdmin (liste, créer, activer/désactiver) | `UserAdminController.java`, `UserAdminService.java`, `UserRepository.java` | 1,5h |
| T1.4 | Auth client séparée : login + inscription + session client | `ClientAuthController.java`, `ClientService.java`, `ClientRepository.java` | 1h |
| T1.5 | Templates : `login.html` admin + `client/inscription.html` + page users admin | `templates/login.html`, `templates/client/inscription.html`, `templates/admin/users.html` | 2h |

**Total : 7h — 2,5 jours (3h/jour)**

---

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

## Miarantsoa | Gestion des Lots

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T2.3 | `LotService` : créer lot, valider capacité bâtiment, calcul âge actuel (environ 5mois au debut) | `LotService.java`, `LotRepository.java`, `BatimentRepository.java` | 1,5h |
| T2.4 | `LotController` : CRUD + filtre statut/bâtiment | `LotController.java`, `LotService.java` | 1h |
| T2.5 | Action réforme : statut → "reformé", date_reforme, redirection vers vente | `LotService.java`, `LotController.java`, `model/Lot.java` | 0,45h |
| T2.6 | Template `lots/liste.html` + `lots/form.html` | `templates/lots/liste.html`, `templates/lots/form.html` | 1h |
| T2.7 | Template `lots/detail.html` : onglets Bootstrap (production, mortalité, traitements…) + bouton réformer | `templates/lots/detail.html` | 2h |

**Total : 5,95h — 2 jours (3h/jour)**

---

## Josué | Santé & Mortalité

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T3.1 | `MortService` : enregistrer + update `nombre_actuel` + alerte si taux élevé | `MortService.java`, `MortRepository.java`, `model/Mortalite.java` | 1h |
| T3.2 | `MortController` + `TraitementController` | `MortController.java`, `TraitementController.java` | 1h |
| T3.3 | `TraitementService` : enregistrer + créer `MvtArgent` auto (traitement_veterinaire) | `TraitementService.java`, `MvtArgentService.java`, `TraitementRepository.java` | 1h |
| T3.4 | Templates formulaires mortalité + traitements (intégrés dans onglets `lots/detail.html`) | `templates/lots/detail.html` | 1,5h |
| T3.5 | Historique mortalité + graphique Chart.js LineChart 30 jours + historique traitements | `templates/mortalite/historique.html`, `templates/traitements/historique.html` | 2h |

**Total : 6,5h — 2,5 jours (3h/jour)**

---

## Vinod | Production d'œufs

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T4.1 | `OeufProductionService` : saisie (1/lot/jour, pas de date future) + `OeufStatutService` | `OeufProductionService.java`, `OeufStatutService.java`, `OeufProductionRepository.java` | 1h |
| T4.2 | `OeufService.getStockDisponible()` : méthode partagée utilisée par P8 (Izaia) | `OeufService.java` | 1,5h |
| T4.3 | Calcul taux de ponte par lot (% réel vs rendement attendu de la race) | `OeufProductionService.java`, `RaceRepository.java` | 1,5h |
| T4.4 | Template `oeufs/saisie.html` + `oeufs/stats.html` + Chart.js BarChart 14 jours | `templates/oeufs/saisie.html`, `templates/oeufs/stats.html` | 2h |

**Total : 6h — 2 jours (3h/jour)**

---

## Chef de projet par interim: Idealy | Employés & Salaires

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T6.1 | CRUD `Employe` + toggle actif/inactif (soft delete) | `EmployeController.java`, `EmployeService.java`, `EmployeRepository.java`, `model/Employe.java` | 1,5h |
| T6.2 | `PaiementSalaireService` : enregistrer + créer `MvtArgent(sortie, salaire)` auto | `PaiementSalaireService.java`, `MvtArgentService.java`, `PaiementSalaireRepository.java` | 2h |
<!-- | T6.3 | Récap mensuel : liste employés + statut payé/non payé pour le mois courant | `PaiementSalaireService.java`, `EmployeRepository.java` | 1h | -->
| T6.4 | Templates : liste employés + form CRUD + historique paiements + récap mensuel | `templates/employes/liste.html`, `templates/employes/form.html`, `templates/employes/historique.html`, `templates/employes/recap.html` | 2h |

**Total : 6,5h — 2,5 jours (3h/jour)**

---
***Plus filtre mois avec status***

## Izaia | Ventes

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T7.1 | `VenteService` : créer vente + lignes, calcul total, validation stock œufs (via Vinod) | `VenteService.java`, `VenteRepository.java`, `DetailVenteRepository.java`, `OeufService.java` | 1,5h |
| T7.2 | Si produit = "poule" → `lot_id` obligatoire + lot doit être "reformé" | `VenteService.java`, `LotRepository.java` | 1,25h |
| T7.3 | Action "Marquer payé" → créer `MvtArgent(entrée)` automatiquement | `VenteService.java`, `MvtArgentService.java` | 1h |
| T7.4 | Templates : formulaire multi-lignes dynamique (JS) + liste ventes + détail vente + bouton "Créer livraison" | `templates/ventes/form.html`, `templates/ventes/liste.html`, `templates/ventes/detail.html` | 2,5h |

**Total : 6,25h — 2,5 jours (3h/jour)**

---

## Ricardo | Livraisons & Clients

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T8.1 | `LivraisonService` : créer depuis vente, gestion statuts, frais (mvt argent : depense) → `MvtArgent` auto | `LivraisonService.java`, `LivraisonRepository.java`, `MvtArgentService.java` | 1,5h |
| T8.2 | Passage statut → "en_cours" : `NotificationService.notifierClient()` (ou bien juste changer le status si cote client non fini a temps) | `LivraisonService.java`, `NotificationService.java` | 1,5h |
| T8.3 | Gestion clients admin : accepter / refuser inscriptions en attente (enregistrer dans la table client si acceptee par admin) | `ClientService.java`, `ClientRepository.java`, `ClientController.java` | 1h |
| T8.4 | Templates : liste livraisons + formulaire (pré-rempli) + gestion clients (onglets en_attente / actifs) | `templates/livraisons/liste.html`, `templates/livraisons/form.html`, `templates/admin/clients.html` | 2h |

**Total : 6h — 2 jours (3h/jour)**

---

## Toavina | Finance & Exports

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T9.1 | `FinanceService` : recettes, dépenses, bénéfice, détail par catégorie, coût de revient par lot | `FinanceService.java`, `MvtArgentRepository.java` | 1,5h |
| T9.2 | Export PDF (iText 7) : bon de livraison + rapport finance | `PdfExportService.java`, `pom.xml` (dépendance iText 7) | 1,25h |
| T9.3 | Export Excel (Apache POI) : production œufs, mouvements stock, paiements salaires | `ExcelExportService.java`, `pom.xml` (dépendance Apache POI) | 1,25h |
| T9.4 | Templates : page finance (cartes + Chart.js BarChart 12 mois) + boutons téléchargement PDF/Excel | `templates/finance/index.html` | 2h |

**Total : 6h — 2 jours (3h/jour)**

---

## Midera | Dashboard & Espace client

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T11.1 | `DashboardController` : agrégation œufs_jour, stock, ventes_jour, bénéfice_mois, livraisons_en_cours | `DashboardController.java`, `DashboardService.java` | 1h |
| T11.2 | `NotificationRepository` : lister, marquer lu, compter non lues (utilisé par Harena + Josué) | `NotificationRepository.java`, `NotificationService.java`, `model/Notification.java` | 2,5h |
| T11.3 | Espace client (non prioritaire) : commandes, livraisons, profil (Controller + templates layout client séparé) | `ClientSpaceController.java`, `templates/client/layout.html`, `templates/client/commandes.html`, `templates/client/livraisons.html`, `templates/client/profil.html` | 2h |
| T11.4 | Template `dashboard.html` : 4 cartes + Chart.js LineChart + fragment notification dropdown navbar | `templates/dashboard.html`, `templates/fragments/notifications.html` | 2h |

**Total : 7,5h — 2,5 jours (3h/jour)**

---

##  Récapitulatif général — Projet réel (TJM marché malgache, fourchette moyenne)

| Membre | Rôle | Total heures | Jours (3h/jour) | TJM (Ar) | Budget (Ar) |
|--------|------|-------------|-----------------|-----------|-------------|
| Estelle | Chef de projet · Architecture | 13,5h | 4,5 jours | 175 000 | 787 500 |
| Toky | Auth & Sécurité (Senior) | 7h | 2,5 jours | 135 000 | 337 500 |
| Harena | Race + Bâtiment + Stock Sakafo | 7h | 2,5 jours | 100 000 | 250 000 |
| Miharantsoa | Gestion des Lots | 5,95h | 2 jours | 100 000 | 200 000 |
| Josué | Santé & Mortalité | 6,5h | 2,5 jours | 100 000 | 250 000 |
| Vinod | Production d'œufs | 6h | 2 jours | 100 000 | 200 000 |
| Idealy | Chef de projet p.i. · Employés & Salaires | 6,5h | 2,5 jours | 150 000 | 375 000 |
| Izaia | Ventes (confirmé) | 6,25h | 2,5 jours | 100 000 | 250 000 |
| Ricardo | Livraisons & Clients | 6h | 2 jours | 100 000 | 200 000 |
| Toavina | Finance & Exports (Senior) | 6h | 2 jours | 135 000 | 270 000 |
| Midera | Dashboard & Espace client (Senior) | 7,5h | 2,5 jours | 135 000 | 337 500 |
| | | | | **TOTAL** | **3 457 500 Ar** |

---

## Planning 5 semaines (3h/jour, parallèles + dépendances)

| Semaine | Jours | Qui travaille | Ce qui se passe |
|---------|-------|--------------|-----------------|
| **S1** | J1–J3 | Estelle seule | T0.1 → T0.4 : init, entités, layout, Git — **tout le monde attend** |
| **S1** | J4–J5 | Toky + Harena en parallèle | Toky démarre Auth (T1.1/T1.2), Harena démarre Race+Bâtiment (T2.1/T2.2) |
| **S2** | J1–J5 | Toky + Harena finissent + Miarantsoa + Josué + Vinod + Idealy démarrent | Auth finie J2, Harena finit Sakafo J5 — Lots, Santé, Œufs, RH démarrent dès J1 S2 |
| **S3** | J1–J2 | Miarantsoa + Josué + Vinod + Idealy finissent | Tous ces modules bouclés en début S3 |
| **S3** | J2–J5 | Izaia démarre (attend T4.2 de Vinod) + Toavina démarre | Ventes + Finance en parallèle, indépendants l'un de l'autre |
| **S4** | J1–J2 | Izaia finit + Ricardo démarre (attend Izaia) | Ventes livrées → Ricardo peut commencer Livraisons |
| **S4** | J1–J5 | Midera démarre (attend Toky + Miarantsoa, dispo depuis S2/S3) + Toavina finit | Dashboard + Espace client + Finance exports |
| **S5** | J1–J2 | Ricardo finit + Midera finit | Livraisons et Dashboard complets |
| **S5** | J3–J5 | Estelle : revues de code + déploiement final (T0.5) | Intégration, tests, corrections, mise en production |

---
<!-- 
### Points critiques sur 5 semaines

| Risque | Membre concerné | Solution |
|--------|----------------|----------|
| **Estelle doit finir T0.1→T0.4 en 3 jours max** | Estelle | Priorité absolue J1–J3 S1, pas de débordement |
| **Vinod doit livrer T4.2 en priorité** | Vinod | Faire T4.2 en tout premier, Izaia est bloqué sans ça |
| **Midera dépend de beaucoup de monde** | Midera | Commencer T11.2 (Notifications) dès S3 sans attendre le reste |
| **Ricardo ne peut pas commencer avant qu'Izaia finisse** | Ricardo + Izaia | Izaia doit livrer au plus tard J2 S4 |
| **T0.5 (revues Estelle) réduit à S5 seulement** | Estelle | Faire des micro-revues chaque soir de S2→S4 pour ne pas tout garder en S5 | -->