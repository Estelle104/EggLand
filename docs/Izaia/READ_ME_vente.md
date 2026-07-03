# Module Vente 

## Critere de validation 
 # | Fonctionnalité | Critère de validation |
|---|----------------|------------------------|
| V1 | Enregistrer une vente| Voir si le stock d'oeuf et le nombre de poule dans un lot est suffisant |
| V2 | Ajouter un statut oeuf | apres validation, une ligne existe dans `OeufStatut` avec le statut vendu|
| V3 | Ajouter un statut au lot | Apres validation du vente , une statut reformer au lot vendue dans `statutlot` |
| V4 | Affichage des liste de vente| verifier si il y a des ventes existant |
| V5 | Faire le filtre des ventes | Verifier les informations dans la base (table `vente` ) |
| V6 | Affichage detail vente | voir les donnees dans la base de donnee (table `vente` `detailvente`) |
| V7 | Modifier une vente | faire un update dans la base pour la vente (table `vente` `detailvente`) |
| V8 | Suppression d'une vente | faire un drop dans la base (table `vente` `detailvente`) |
| V9 | Mettre un statut | directement mettre un statut vendue directement apres vente(table `statutvente`) |

--- 

## 2. Règles de gestion

- **RG1 — Une vente ne peut etre creer si lot et oeuf sont inexistant .**
- **RG2 — On ne peut pas vendre au dessus du stock disponible cela vaut pour l'oeuf et les poules .**
- **RG3 — On peut vendre plusieur fois en une seule journee.**
- **RG4 — On peut vendre plusieur fois pour un seul client .**
- **RG5 — Une vente passe directement a l'etat vendue apres validation.**
- **RG6 — On peut faire une recherche par date , statut et par client.**

---

## 3. Règles d'organisation
- **RO1 — Séparation Controller / Service / Repository.**
  - Le `Controller` ne contient **aucune logique métier** : il reçoit la requête HTTP, convertit les paramètres, appelle le service, et redirige .
  - Le `Service` contient toute la logique métier et les règles de gestion .
  - Le `Repository` ne fait que des accès base de données (aucune logique).

- **RO2 — Les erreurs métier sont des `IllegalArgumentException`.** Le service lève cette exception avec un message clair et destiné à l'utilisateur final. Le controller l'attrape et la transmet à la vue via `RedirectAttributes` (`flash attribute "erreur"`), jamais via une page d'erreur Spring générique.

- **RO3 —Les view html.**
    - `formulairecreation.html` : Faire la saisie de vente 
    - `formulaireModification.html` : Faire la saisie de modification d'une vente
    - `detailVente.html` : afficher les details d'une vente
    - `listeVente.html` : afficher la liste des ventes
    