# Vinod | Production d'œufs

## Taches

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T4.1 | `OeufProductionService` : saisie (1/lot/jour, pas de date future) + `OeufStatutService` | `OeufProductionService.java`, `OeufStatutService.java`, `OeufProductionRepository.java` | 1h |
| T4.2 | `OeufService.getStockDisponible()` : méthode partagée utilisée par P8 (Izaia) | `OeufService.java` | 1,5h |
| T4.3 | Calcul taux de ponte par lot (% réel vs rendement attendu de la race) | `OeufProductionService.java`, `RaceRepository.java` | 1,5h |
| T4.4 | Template `oeufs/saisie.html` + `oeufs/stats.html` + Chart.js BarChart 14 jours | `templates/oeufs/saisie.html`, `templates/oeufs/stats.html` | 2h |

## Taches supplementaires
| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T4.5 | Historique production | `OeufProductionService.java`, `OeufStatutService.java`, `OeufProductionRepository.java` | 1h |
| T4.6 | Livraisons en cours pour Client | `client/livraison.html` | 1,5h |

**Total : 6h — 2 jours (3h/jour)**

**T4.1 - Logique metier pour la saisie d'oeufs**
- Un lot ne peut avoir qu'une seule production par jour.
- La date de production ne doit pas être dans le futur.
- Chaque production peut être répartie par statut : casse, consomme
- Un statut "valide" pour quantite reel (oeufs produit - total oeufs perdu)

- Proceder :
    * Creation des services `OeufProductionService` et `OeufStatutService`
    * Ajout du statut "valide" dans `table.sql`
    * Ajout du repository `StatutOeufRepository`
    * Modification de `OeufStatut` pour avoir
    * Methode `addOeufProduction` : Ajout de OeufProduction + Verification et ajout OeufStatut

    * `LotService` pour prendre les lots (utile pour tester)
    * `OeufController` pour relier au template `saisie.html`
    * `oeuf-saisie.js` pour l'ajout des statuts

**T4,2 - Prendre le stock disponible**
- Puisque la quantite d'oeufs valide par Lot est calculer et stocker dans OeufStatut avec statut "Valide", Le stock disponible en est la somme
- Proceder :
    * Creation de `OeufStatutRepository` 
    * Methode dans `OeufStatutRepository` pour le calcul du stock (oeufs valide)
    * Methode `getStockDisponible` dans `OeufService`

**T4.3 - Calcul taux de ponte par lot**
- Calcul du taux de ponte par lot (par jour) : production reel / nb oeuf attendu * 100
- Proceder :
    * Methode `buildTauxPonteParLot` pour le calcul et stockage des donnees pour un lot
    * Methode `getTauxPondeParLot` pour faire le calcul pour chaque lot (puisque ca parcours tous les productions)
    * 
*
**T4.4 - Templates**
- Attribuer le resultat du stock dans le mapping `/oeufs` et afficher dans le view `stats.html`
- Attribuer les resultat des calcul du taux de ponte dans le mapping `/oeufs` et afficher dans le view `stats.html`
- Creer `chart.js` et creer le chart

**T4.5 - Historique**
- view pour l'historique `v_historique_production`
- utiliser dans repository et dans service
- afficher dans un template `historique.html`

**T4.6 - Livraisons Client**
- fonction dans repository pour prendre livraisons en cours
- afficher dans `client/livraison.html`

**Autre**
- Methodes supplementaire dans `OeufProductionService` pour les regles metiers


