# Exemple de format Excel - Import vente

Ce fichier sert de modele pour l'import Excel des ventes depuis la page `/admin/exports`.

## Fichier attendu

- Format recommande : `.xlsx`
- Premiere feuille du classeur
- Ligne 1 : en-tetes
- Donnees a partir de la ligne 2

## Colonnes Excel

| Colonne | En-tete conseille | Exemple | Regle |
| --- | --- | --- | --- |
| A | Nom client | Rakoto | Le client doit exister. La recherche se fait sur le nom. |
| B | Date vente | 09/07/2026 | Format `dd/MM/yyyy` ou vraie cellule date Excel. |
| C | Code produit | oeuf | Le code doit exister dans `ProduitVente`. |
| D | Quantite | 30 | Nombre. |
| E | Prix unitaire | 800 | Nombre en ariary. |

## Exemple a copier dans Excel

| Nom client | Date vente | Code produit | Quantite | Prix unitaire |
| --- | --- | --- | ---: | ---: |
| Rakoto | 09/07/2026 | oeuf | 30 | 800 |
| Rakoto | 09/07/2026 | poule | 2 | 25000 |
| Rakoto | 10/07/2026 | fumier | 5 | 3000 |

## Valeurs valides d'apres les SQL fournis

Clients disponibles dans `1-donnee-initiale.sql` :

| Nom | Prenom |
| --- | --- |
| Rakoto | Jean |

Produits disponibles dans `1-donnee-initiale.sql` :

| Code produit |
| --- |
| oeuf |
| poule |
| fumier |

Statut applique automatiquement a l'import :

| Code statut |
| --- |
| en_attente |

## Resultat de l'import

Pour chaque ligne valide :

- une ligne est creee dans `Vente` ;
- une ligne est creee dans `DetailVente` ;
- le total est calcule avec `Quantite * Prix unitaire` ;
- la vente prend automatiquement le statut `en_attente`.

## Erreurs courantes

| Erreur | Cause probable |
| --- | --- |
| Client introuvable | La colonne A ne correspond a aucun client existant. |
| Produit introuvable | La colonne C n'est pas un code produit valide. |
| Donnees manquantes | Une cellule obligatoire est vide entre A et E. |
| Erreur de date | La colonne B n'est pas une date Excel ou une date au format `dd/MM/yyyy`. |
