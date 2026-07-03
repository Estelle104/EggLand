# Module Production oeufs — Spécifications 

## A verifier
- Le view sql `v_historique_production` est dans la base 

## 1. Critères de validation

| # | Fonctionnalité | Critère de validation |
|---|----------------|------------------------|
| V1 | Enregistrer une production d'oeufs| Une nouvelle ligne existe dans la table `OeufProduction` et `OeufStatut` avec les quantites correctes. La production est afficher dans historique. |
| V2 | Ajouter un statut | apres validation, une ligne existe dans `OeufStatut` avec le statut correcte|
| V3 | Affichage stock | Faire la somme des quantites avec statut valides pour tous les lots (requete dans base ou calcul) |
| V4 | Affichage du taux de ponte par lot, par jour| Calcul a la main/calculatrice |
| V5 | Affichage du barchart des productions (14 dernieres jours) | Verifier les informations dans la base (table `OeufProduction`) |
| V6 | Affichage historique | Comparer avec le view `v_historique_production` ou par requete sql |
| V7 | Modifier l'historique | Comparer avec le view `v_historique_production`, par requete sql, dans les tables `OeufProduction` et `OeufStatut` |
| V8 | Suppression historique | Comparer avec le view `v_historique_production`, par requete sql, dans les tables `OeufProduction` et `OeufStatut` |

---

## 2. Règles de gestion

- **RG1 — Une collecte ne peut pas être enregistrée dans le futur.**
- **RG2 — La quantité produite et la quantite des oeufs (casse/consomme) doit être strictement positive.** 
- **RG3 — Le total des oeufs (casse/consomme) ne peut pas depasser le total produit** 
- **RG4 — La date de collecte ne peut pas être avant la date d’arrivée du lot.**
- **RG5 — On ne peut pas avoir deux productions pour le même lot à la même date.**
- **RG6 — Le statut valide ne peut pas être saisi manuellement: il est calculé automatiquement.**
- **RG7 — Le statut vendu ne peut pas être saisi pendant une collecte.**
- **RG8 — La quantité valide est calculée ainsi: quantite totale - total anomalies.**
- **RG9 — Le graphique des 14 derniers jours couvre aujourd’hui et les 13 jours précédents, avec 0 pour les jours sans production.**
- **RG10 — Le taux de ponte par lot utilise seulement la quantité valide.**
- **RG11 — Pour calculer le taux, le lot doit avoir au moins une race associée.**
- **RG12 — Si le stock calculé devient négatif, le service considère le stock incohérent et lève une erreur.**

---

## 3. Règles d'organisation

- **RO1 — Séparation Controller / Service / Repository.**
  - Le `Controller` ne contient **aucune logique métier** : il reçoit la requête HTTP, convertit les paramètres, appelle le service, et redirige.
  - Le `Service` contient toute la logique métier et les règles de gestion (RG1 à RG7).
  - Le `Repository` ne fait que des accès base de données (aucune logique).

- **RO2 — Les erreurs métier sont des `IllegalArgumentException`.** Le service lève cette exception avec un message clair et destiné à l'utilisateur final. Le controller l'attrape et la transmet à la vue via `RedirectAttributes` (`flash attribute "erreur"`), jamais via une page d'erreur Spring générique.


- **RO3 — Une vue = un seul objectif.**
  - `saisie.html` : Enregistrer un collecte des oeufs produit d'un lot a une date.
  - `stats.html` : Afficher le stock d'oeufs, les productions des 14 dernieres jours et le taux de ponte de chaque production par lot.
  - `historique.html` : consulter le détail de tous les productions et peut etre modifier en cas d'erreur humaine.

- **RO5 — Toute date métier (mois, date de versement, date d'embauche) est un `LocalDate`**, jamais une chaîne, dès qu'elle entre dans la couche service. La conversion `String → LocalDate` se fait uniquement dans le `Controller`.
