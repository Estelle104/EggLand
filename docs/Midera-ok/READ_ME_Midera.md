# Module Dashboard, Notifications, Espace Client & Mortalité — Spécifications

## 1. Critères de validation

| # | Fonctionnalité | Critère de validation |
|---|----------------|------------------------|
| V1 | Agrégation des métriques du dashboard | L'appel à `GET /dashboard` retourne les cinq métriques (`oeufsJour`, `stockDisponible`, `ventesJour`, `beneficeMois`, `livraisonsEnCours`) avec des valeurs cohérentes issues des tables `oeuf_production`, `vente`, `mvt_argent` et `livraison`. |
| V2 | Affichage du graphique de production | Le canvas `#chartProduction` affiche un graphique Chart.js de type **Line** contenant les dates et les quantités d'œufs produits sur les 14 derniers jours, sans erreur JavaScript. |
| V3 | Création d'une notification | Après appel à `notificationService.creer(type)`, une ligne est insérée dans la table `notification` avec les champs `type`, `message`, `date_creation` non nuls et `lu = false`. |
| V4 | Compteur des notifications non lues | `notificationService.compterNonLues()` retourne exactement le nombre de notifications où `lu = false`. |
| V5 | Marquage d'une notification comme lue | Après `notificationService.marquerCommeLu(id)`, la notification possède `lu = true` en base. |
| V6 | Notification de stock faible | Un mouvement de stock qui passe sous le seuil d'alerte déclenche `notificationService.creer("STOCK_FAIBLE")` et une notification persistée apparaît. |
| V7 | Espace client — Commandes | `GET /client/espace/commandes` affiche uniquement les commandes du client connecté (identifié par son email). |
| V8 | Espace client — Livraisons | `GET /client/espace/livraisons` affiche uniquement les livraisons du client connecté. |
| V9 | Espace client — Profil | `GET /client/espace/profil` affiche les informations personnelles du client (nom, email, téléphone, adresse). |
| V10 | Redirection post-login client | Après authentification, un utilisateur ayant le rôle **CLIENT** est redirigé vers `/client/espace/commandes`. |
| V11 | Dropdown notifications | L'icône cloche affiche un badge avec le nombre de notifications non lues. Le clic ouvre un menu déroulant listant les notifications. |
| V12 | Filtre mortalité par lot | Sélectionner un lot puis cliquer sur **Appliquer** affiche uniquement les morts de ce lot. |
| V13 | Filtre mortalité par dates | Changer la date de début et la date de fin puis cliquer sur **Appliquer** limite les morts affichées à la période sélectionnée. |
| V14 | Camembert mortalité | Le graphique `#chartMortalite` affiche un diagramme en camembert avec **Rouge = morts** et **Vert = vivants**, ainsi qu'un tooltip indiquant le pourcentage. |
| V15 | Notification seuil mortalité | Lorsque le nombre de morts d'un lot dépasse `configuration.seuil_mort`, une notification persistée **"Seuil atteint : X morts dans le lot Y"** est créée. |

---

## 2. Règles de gestion

- **RG1 — Les métriques du dashboard sont calculées en temps réel.** Chaque appel à `/dashboard` interroge directement la base de données, sans cache.

- **RG2 — Les notifications sont persistées.** Une notification créée reste enregistrée en base jusqu'à suppression manuelle.

- **RG3 — L'espace client est strictement personnel.** Un client ne voit que ses propres commandes, livraisons et informations personnelles.

- **RG4 — L'accès à l'espace client nécessite le rôle `CLIENT` ou `ADMIN`.** Un gestionnaire non administrateur ne peut pas accéder à `/client/**`.

- **RG5 — Le nombre de vivants est calculé selon la formule :** `Nombre initial - SUM(morts) - SUM(réformes)`. Si le résultat est négatif, il est ramené à 0.

- **RG6 — Une notification de seuil est créée à chaque chargement du dashboard lorsque le lot dépasse le seuil de mortalité.** Les notifications ne sont pas dédupliquées.

---

## 3. Règles d'organisation

- **RO1 — Séparation Controller / Service / Repository.**
  - Le `DashboardController` et le `ClientSpaceController` reçoivent les requêtes HTTP, appellent les services et retournent les vues.
  - Les services (`DashboardService`, `MortService`, `NotificationService`) contiennent toute la logique métier.
  - Les repositories contiennent uniquement les accès aux données (`@Query` ou requêtes JPA dérivées).

- **RO2 — Les erreurs métier sont des `RuntimeException` ou `IllegalArgumentException`.** Elles sont gérées par les contrôleurs via `RedirectAttributes` ou une page d'erreur.

- **RO3 — Une vue = un objectif.**
  - `dashboard/index.html` : tableau de bord.
  - `client/espace/commandes.html` : commandes du client.
  - `client/espace/livraisons.html` : livraisons du client.
  - `client/espace/profil.html` : profil du client.
  - `fragments/notifications.html` : fragment de notifications.

- **RO4 — Toutes les données monétaires sont des `BigDecimal`.**

- **RO5 — Toutes les données quantitatives (`oeufsJour`, `stockDisponible`, `morts`, `vivants`) sont des `Integer`.**

- **RO6 — Les fragments Thymeleaf sont privilégiés.** `notifications.html` est inclus dans `header.html` via `th:replace`.