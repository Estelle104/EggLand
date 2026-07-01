# Explication :

## Repository :

- `OeufProductionRepository` : 
    * `sumQuantiteTotale` calcule la quantite totale d'oeufs produits
    * `sumQuantiteByDate` et `sumQuantiteParDate` calcule la quantite totale a une date ou entre deux date
    * `findHistoriqueProduction` prend les donnees de la view `v_historique_production`, paginer ou non
    * `findByDateBetweenOrderByDateDesc` trouve toutes les productions fait entre deux dates tries du plus récent au plus ancien , puis triees par leur ID, du plus récent au plus ancien.

    * `findAllByOrderByDateDescIdDesc` trouve toutes les productions tries du plus récent au plus ancien, puis triees par leur ID, du plus récent au plus ancien.

    * `existsByLotIdAndDate` verifie s'il existe une production de meme date et meme lot
    * `existsByLotIdAndDateAndIdNot` verifie s'il existe une production de meme date, meme lot mais different id
- `OeufStatutRepository` :
    * `sumQuantiteByStatutId` a pour but de calculer les quantites d'oeufs valide
    * `sumQuantiteIndisponible` calcule tout les oeufs indisponible
    * `findAllByOrderByProductionDateDescIdDesc` 
    * `findByProductionIdAndStatutCode`
    * `findFirstByStatutCodeAndQuantiteGreaterThan`
    * `findByStatutCodeOrderByProductionDateAsc`
- `StatutOeufRepository` :
    * `findByCode` 
    * `findAllByOrderByCodeAsc`

## Service :



