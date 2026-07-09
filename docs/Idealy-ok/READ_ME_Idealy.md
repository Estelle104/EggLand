# Module Employés & Salaires — Spécifications

## 1. Critères de validation


| # | Fonctionnalité | Critère de validation |
|---|----------------|------------------------|
| V1 | Créer un employé | Après clic sur "Enregistrer", une nouvelle ligne existe dans la table `employe` avec les bonnes valeurs (nom, prénom, tél, salaire, date d'embauche). L'utilisateur est redirigé vers `/employes` et voit l'employé dans la liste. |
| V2 | Modifier un employé | Après clic sur "Enregistrer" dans la modale Modifier, la ligne `employe` correspondante est mise à jour en base avec les nouvelles valeurs. La liste affiche les valeurs modifiées sans rechargement manuel. |
| V3 | Supprimer un employé | Après confirmation, la ligne `employe` n'existe plus dans la table. Il disparaît de la liste. |
| V4 | Refuser une date d'embauche future | Si la date d'embauche saisie est postérieure à la date du jour, l'enregistrement est refusé : aucune ligne n'est créée/modifiée en base, et un message d'erreur apparaît à l'écran. |
| V5 | Verser un salaire (cas normal) | Après "Enregistrer le versement" : <br>• une ligne est ajoutée dans `versementsalaire` (montant, date saisis) <br>• la ligne `paiementsalaire` correspondante (employé + mois) a son `montant` cumulé augmenté du montant versé, et `date_paiement` mise à la date saisie <br>• une ligne est ajoutée dans `mvtargent` avec `type = sortie`, `categorie = salaire`, `montant` = montant versé, `date` = date saisie. |
| V6 | Bloquer un versement antérieur à l'embauche | Si le mois choisi est avant le mois d'embauche, OU si la date du versement est avant la date d'embauche : aucune ligne n'est créée dans `versementsalaire`/`paiementsalaire`/`mvtargent`, et un message d'erreur explicite est affiché sur la page récap. |
| V7 | Passage à "Payé" à 100% | Quand le cumul versé (`paiementsalaire.montant`) atteint ou dépasse le salaire de l'employé, `paiementsalaire.paye` passe à `true`, le bouton "Payer" disparaît du récap et est remplacé par "Réglé". |
| V8 | Affichage du pourcentage | Le récap affiche `(montant cumulé / salaire) × 100`, arrondi à l'entier inférieur, plafonné à 100%. |
| V9 | Filtrage historique/récap | Changer le filtre "Mois" ou "Statut" recharge la page avec uniquement les lignes correspondantes (vérifiable en changeant le filtre et en comptant les lignes affichées). |
| V10 | Détail des versements dans l'historique | Pour un employé/mois donné, l'historique affiche bien **chaque** ligne de `versementsalaire` liée à ce `paiementsalaire` (et pas seulement le total). |

--- 

## 2. Règles de gestion

- **RG1 — Un employé a une date d'embauche obligatoire.**
- **RG2 — On ne paie pas un salaire avant l'embauche.** Un versement de salaire est refusé si :
  - le mois du salaire est antérieur au mois d'embauche, **ou**
  - la date du versement est antérieure à la date d'embauche.
- **RG3 — Le salaire peut être versé en plusieurs fois (paiement partiel).** Chaque versement est conservé individuellement (date + montant). Le cumul des versements d'un même employé pour un même mois déterminé son statut payé/non payé.
- **RG4 — Un mois est "payé" quand le cumul versé atteint le salaire dû.** Tant que le cumul est inférieur au salaire, le statut reste "non payé", même si plusieurs versements ont déjà eu lieu.
- **RG5 — Chaque versement génère un mouvement financier.** Un versement de salaire crée toujours un `MvtArgent` de type `sortie` et de catégorie `salaire`, avec le **même montant** et la **même date** que le versement (pas la date du jour).
- **RG6 — Un employé ne peut pas être payé deux fois pour le même montant sans qu'on le redemande explicitement.** Chaque clic sur "Enregistrer le versement" crée un nouveau versement ; il n'y a pas de paiement automatique déclenché par le système.
- **RG7 — La suppression d'un employé est définitive.** Il n'y a pas de mécanisme d'archivage/désactivation dans cette version : un employé supprimé n'apparaît plus dans aucune liste.

---

## 3. Règles d'organisation

- **RO1 — Séparation Controller / Service / Repository.**
  - Le `Controller` ne contient **aucune logique métier** : il reçoit la requête HTTP, convertit les paramètres, appelle le service, et redirige.
  - Le `Service` contient toute la logique métier et les règles de gestion (RG1 à RG7).
  - Le `Repository` ne fait que des accès base de données (aucune logique).

- **RO2 — Les erreurs métier sont des `IllegalArgumentException`.** Le service lève cette exception avec un message clair et destiné à l'utilisateur final. Le controller l'attrape et la transmet à la vue via `RedirectAttributes` (`flash attribute "erreur"`), jamais via une page d'erreur Spring générique.

- **RO3 — Une vue = un seul objectif.**
  - `liste.html` : consulter/modifier/supprimer un employé.
  - `form.html` : créer un employé (et réutilisé pour l'édition complète si besoin).
  - `recap.html` : voir et agir sur le statut de paiement du mois en cours.
  - `historique.html` : consulter (lecture seule) le détail de tous les versements passés.

- **RO4 — Toute donnée monétaire est un `BigDecimal`**, jamais un `double`/`float`, pour éviter les erreurs d'arrondi sur les montants en Ariary.

- **RO5 — Toute date métier (mois, date de versement, date d'embauche) est un `LocalDate`**, jamais une chaîne, dès qu'elle entre dans la couche service. La conversion `String → LocalDate` se fait uniquement dans le `Controller`.
