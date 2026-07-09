# Module Employés & Salaires
## 1. Critères de validation

| # | Fonctionnalité | Critère de validation |
|---|----------------|------------------|
| V1 | Créer un employé | Une nouvelle ligne apparaît dans `employe` en base. L'employé est visible dans la liste. |
| V2 | Modifier un employé | La ligne `employe` est mise à jour en base. La liste affiche les nouvelles valeurs. |
| V3 | Supprimer un employé | La ligne `employe` n'existe plus en base. Il disparaît de la liste. |
| V4 | Refuser une date d'embauche future | Si la date est après aujourd'hui : rien n'est enregistré, un message d'erreur s'affiche. |
| V5 | Verser un salaire | Une ligne est créée dans `versementsalaire`. Le cumul dans `paiementsalaire` augmente. Une ligne est créée dans `mvtargent` (type `sortie`, catégorie `salaire`, même montant et même date que le versement). |
| V6 | Bloquer un versement avant l'embauche | Si le mois ou la date du versement est avant l'embauche : rien n'est enregistré, un message d'erreur s'affiche sur le récap. |
| V7 | Passage à "Payé" à 100% | Quand le cumul versé atteint le salaire dû : `paiementsalaire.paye` passe à `true`, le bouton "Payer" est remplacé par "Réglé". |
| V8 | Bloquer un dépassement de salaire | Si le montant saisi ferait dépasser le salaire dû : rien n'est enregistré, un message d'erreur s'affiche. |
| V9 | Affichage du pourcentage | Le récap affiche `(montant versé / salaire) × 100`, arrondi à l'entier inférieur, max 100%. |
| V10 | Filtrage historique/récap | Changer le filtre "Mois" ou "Statut" affiche uniquement les lignes correspondantes. |
| V11 | Détail des versements dans l'historique | Pour un employé/mois, l'historique liste chaque versement individuel (date + montant), pas seulement le total. |

---

## 2. Règles de gestion

- **RG1 — Date d'embauche obligatoire.** On ne peut pas créer ou modifier un employé sans date d'embauche.
- **RG2 — Pas de salaire avant l'embauche.** Un versement est refusé si le mois choisi ou la date du versement est avant la date d'embauche de l'employé.
- **RG3 — Paiement partiel autorisé.** On peut payer un salaire en plusieurs fois. Chaque versement est sauvegardé séparément avec sa propre date et son propre montant.
- **RG4 — "Payé" = cumul atteint le salaire.** Tant que le total versé est inférieur au salaire du mois, le statut reste "non payé", peu importe le nombre de versements déjà faits.
- **RG5 — Chaque versement enregistre une sortie d'argent.** Un `MvtArgent` (type `sortie`, catégorie `salaire`) est créé automatiquement à chaque versement, avec le même montant et la même date que le versement saisi.
- **RG6 — Pas de dépassement du salaire dû.** On ne peut pas verser plus que le salaire du mois. Si le montant saisi dépasse ce qui reste à payer, le versement est refusé.
- **RG7 — Aucun versement automatique.** Un versement n'est créé que si l'utilisateur remplit et soumet le formulaire. Rien ne se passe automatiquement (pas de paiement planifié, pas de déclenchement par date).
- **RG8 — Suppression définitive.** Un employé supprimé disparaît de toutes les listes. Il n'y a pas d'archivage.

---

## 3. Règles d'organisation

- **RO1 — Controller / Service / Repository ont chacun leur rôle.**
  - `Controller` : reçoit la requête, convertit les paramètres, appelle le service, redirige. Pas de logique métier.
  - `Service` : contient toutes les règles métier (RG1 à RG8).
  - `Repository` : accès base de données uniquement. Pas de logique.

- **RO2 — Les erreurs s'affichent sur la page, pas en page blanche.** Le service lève une `IllegalArgumentException` avec un message lisible. Le controller la récupère et l'envoie à la vue via un flash attribute `"erreur"`.

- **RO3 — Une vue = un seul rôle.**
  - `liste.html` : voir, modifier, supprimer les employés.
  - `form.html` : créer un employé.
  - `recap.html` : voir le statut de paiement du mois et enregistrer des versements.
  - `historique.html` : consulter les versements passés (lecture seule).

- **RO4 — Les dates sont toujours en `LocalDate` dans le service.** La conversion depuis le texte du formulaire se fait uniquement dans le Controller.

- **RO5 — Routes du module :**
  - `GET /admin/employes` -> liste des employés
  - `POST /admin/employes/nouveau` -> créer un employé
  - `POST /admin/employes/{id}/modifier` -> modifier un employé
  - `POST /admin/employes/{id}/supprimer` -> supprimer un employé
  - `GET /admin/employes/recap` -> récap mensuel des salaires
  - `POST /admin/employes/{id}/verser` -> enregistrer un versement
  - `GET /admin/employes/historique` -> historique des versements

- **RO6 — Le CSS est partagé.** Toutes les pages utilisent `components.css`. On n'écrit pas de style directement dans les pages HTML.