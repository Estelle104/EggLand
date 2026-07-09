# Module Lots


## A faire avant tout
        - Voir la page function.sql et inserer les vues dans le function:
                ce sont des vues qui va permettre de faire le lien entre le lots et les autres tables qui ont un liens avec lotsz'"q
        - Voir la page ajout.sql et inserer les commandes dans cette page 

        
## 1. Gestion de Lots 

| # | Fonctionnalité | Critère de validation |
|---|----------------|------------------|
| V1 | Créer un Lots | Une nouvelle ligne apparaît dans `lot` en base et dans `lot_races`. Lot est visible dans la liste. |
| V2 | Verifier Capaciter Lots | Avant insertion d'un `lot` on verifie d'abord le nombre de poule pour qu'il ne depasse pas la capacite d'un batiment |
| V3 | Calculer age actuel | Pour le calcul d'age on fait depart 24 semaine a la date d'entrer d'un lot et il calcul l'age par semaine |
| V4 | Liste de Lots | Les lots est visible dans la liste. |
| V5 | Filtre par Date et Statut | On prends les listes de lots avec les batiments ou statut demandé |
| V6 | Reformer un Lots | Quand on clique sur reformer ça affiche un formulaire avec une date : Ajoute une nouvelle ligne `reforme` et fait un update du statut du lot choisi|
| V7 | Modifier un Lots | Dans la modification on affiche un formulaire generique dans la page liste et on modifie un lot|
| V8 | Calculer Capacite | On calcul la capacite du batiment en excluant celle du race modifier |
| V9 | Detail Lots | On prends les details des lots dans v_lots_detail qui incluent le nombre total d'oeufs , nombre restat de poulet et le dernier traitement  du lot |
| V10 | Supprimer Lots | Supprimer un Lot |


---

## 2. Règles de gestion


**RG4** : Chaque lot appartient à une seule race.
**RG5** : Chaque lot possède un effectif initial obligatoire.
**RG6** : Un lot ne peut être créé que si la capacité disponible du bâtiment est suffisante.
**RG7** : L'effectif vivant est calculé automatiquement.
**RG8** : L'effectif vivant ne peut jamais être négatif.
**RG9** : Un batiment ne peut pas avoir plusieur lots
---

## 3. Règles d'organisation

- **RO1 — Controller / Service / Repository ont chacun leur rôle.**
  - `Controller` : reçoit la requête, convertit les paramètres, appelle le service, redirige. Pas de logique métier.
  - `Service` : contient toutes les règles métier 
  - `Repository` : accès base de données uniquement. Pas de logique.

- **RO2 — Les erreurs s'affichent sur la page, pas en page blanche.** Le service lève une `IllegalArgumentException` avec un message lisible. Le controller la récupère et l'envoie à la vue via un flash attribute `"error"`.

- **RO3 — Une vue = un seul rôle.**
  - `liste.html` : voir, modifier, supprimer les lots.      
  - `form.html` : créer un lot.
  - `detail.html` : voir le detail de chaque lot.


- **RO4 — Les dates sont toujours en `LocalDate` dans le service.** La conversion depuis le texte du formulaire se fait uniquement dans le Controller.

- **RO5 — Routes du module :**
 - `GET  /admin/lots ` -> afficher la liste des lots
- `GET  /admin/lots/create`-> afficher le formulaire de création d'un lot
- `POST /admin/lots/create`-> créer un nouveau lot
- `POST /admin/lots/modifier/{id}`-> modifier un lot
- `GET  /admin/lots/supprimer/{id}`-> supprimer un lot
- `GET  /admin/lots/detail/{id}`-> afficher les détails d'un lot
- `POST /admin/lots/reforme/{idLot}`-> réformer un lot
- `GET  /admin/lots/api/{id}`-> récupérer un lot (API JSON)
- `GET  /admin/lots/data/races`-> récupérer la liste des races (API JSON)

- **RO6 — Le CSS .** Chaque css est attribuer par un nom avec leur nom de page respectif ou action 
- Ex: liste.html listelots.css