# Module : Finance - Export/Import PDF, Excel

Ce module regroupe le tableau de bord financier, les exports PDF/Excel et l'import Excel des ventes.

## 1 - Critere de validation

### Acces aux pages

- La page Finance est disponible sur `/admin/finance`.
- La page Export / Import est disponible sur `/admin/exports`.
- Ces deux pages sont accessibles aux utilisateurs ayant l'autorite `gestionnaire` ou `admin`.
- Les pages de test sont aussi routees :
  - `/admin/finance/test`
  - `/admin/finance/vraitest`
  - `/admin/exports/test`
  - `/admin/exports/vraitest`

### Tableau de bord Finance

La page `/admin/finance` doit afficher :

- le total des recettes ;
- le total des depenses ;
- le benefice net ;
- la marge ;
- l'evolution recettes/depenses sur les 12 derniers mois ;
- la repartition des recettes par categorie ;
- la repartition des depenses par categorie ;
- le cout de revient par lot.

Les donnees viennent principalement de `MvtArgent` via `FinanceService`.

### Exports PDF

Depuis `/admin/exports`, les exports PDF disponibles sont :

- Bon de livraison par date :
  - route : `GET /admin/finance/export/pdf/bon-livraison?date=YYYY-MM-DD`
  - fichier genere : `bon_livraison_YYYY-MM-DD.pdf`
  - contenu : numero livraison, client, date, adresse, produit, quantite, total quantite et total montant.

- Paiement de salaire par mois :
  - route : `GET /admin/finance/export/pdf/paiement-salaire?mois=YYYY-MM`
  - fichier genere : `paiement_salaire_YYYY-MM.pdf`
  - contenu : employe, mois, montant, statut, date paiement, total.

### Exports Excel

Depuis `/admin/exports`, les exports Excel disponibles sont :

- Production d'oeufs :
  - route : `GET /admin/finance/export/excel/production-oeufs?debut=YYYY-MM-DD&fin=YYYY-MM-DD`
  - fichier genere : `production_oeufs_debut_fin.xlsx`
  - colonnes : Date, Lot, Race, Batiment, Quantite, Statuts.

- Mouvement de stock :
  - route : `GET /admin/finance/export/excel/mouvement-stock?debut=YYYY-MM-DD&fin=YYYY-MM-DD`
  - fichier genere : `mouvement_stock_debut_fin.xlsx`
  - colonnes : Date, Type, Nourriture, Lot, Quantite.

- Paiements salaires :
  - route : `GET /admin/finance/export/excel/paiements-salaires?mois=YYYY-MM`
  - fichier genere : `paiements_salaires_YYYY-MM.xlsx`
  - colonnes : Employe, Mois, Montant (Ar), Statut, Date Paiement.

### Import Excel des ventes

Depuis `/admin/exports`, l'import des ventes se fait avec :

- route : `POST /admin/finance/import/liste-ventes`
- champ fichier : `file`
- formats acceptes dans l'interface : `.xlsx`, `.xls`
- traitement reel : lecture avec Apache POI `XSSFWorkbook`, donc le format `.xlsx` est le format le plus fiable.

Le fichier doit contenir une ligne d'en-tete, puis les donnees a partir de la ligne 2.

Colonnes attendues :

| Colonne | Champ | Format / regle |
| --- | --- | --- |
| A | Nom client | Le client doit exister. Recherche par nom contenant la valeur. |
| B | Date vente | Format `dd/MM/yyyy`. Les cellules date Excel sont aussi acceptees. |
| C | Code produit | Le produit doit exister dans `ProduitVente`. |
| D | Quantite | Nombre. |
| E | Prix unitaire | Nombre. |

Pour chaque ligne valide :

- une `Vente` est creee ;
- un `DetailVente` est cree ;
- le total est calcule avec `quantite * prix unitaire` ;
- le statut de vente utilise le code `en_attente`.

Apres import, la page affiche :

- les lignes importees avec succes ;
- les lignes en erreur ;
- le nombre de succes ;
- le nombre d'erreurs.

## 2 - Regle de gestion

### Finance

- Une recette correspond a un mouvement d'argent dont le type a le code `entree`.
- Une depense correspond a un mouvement d'argent dont le type a le code `sortie`.
- Le benefice net est calcule ainsi :

```text
beneficeNet = totalRecettes - totalDepenses
```

- La marge affichee sur le tableau de bord est calculee ainsi :

```text
marge = beneficeNet / totalRecettes * 100
```

- Si le total des recettes est egal a 0, la marge affichee vaut 0.
- Le cout de revient par lot correspond a la somme des mouvements d'argent de type `sortie` lies a ce lot.
- Les graphiques mensuels affichent les 12 derniers mois a partir du mois courant.

### Export PDF

- Le bon de livraison filtre les livraisons par `dateLivraison`.
- Le PDF du bon de livraison est genere en format A4 paysage.
- Le PDF des salaires filtre les paiements par mois.
- Le mois recu depuis l'interface au format `YYYY-MM` est transforme en premier jour du mois.

### Export Excel

- Les exports avec periode utilisent des dates inclusives : `BETWEEN debut AND fin`.
- Les productions d'oeufs sont triees par date decroissante.
- Les mouvements de stock sont tries par date decroissante.
- Les paiements de salaires sont tries par nom d'employe croissant.
- Les fichiers Excel generes sont au format `.xlsx`.

### Import Excel

- Une ligne vide est ignoree.
- Si une donnee obligatoire est manquante, la ligne est rejetee.
- Si le client est introuvable, la ligne est rejetee.
- Si le produit est introuvable, la ligne est rejetee.
- Si la date, la quantite ou le prix ne peut pas etre converti, la ligne est rejetee.
- L'import est transactionnel au niveau du service.
- Les erreurs sont collectees ligne par ligne pour etre affichees a l'utilisateur.

## 3 - Regle d'organisation

### Controleurs

- `ExportController`
  - gere la page `/admin/exports` ;
  - initialise les valeurs par defaut des champs date :
    - `today`
    - `todayMinus30`
    - `currentMonth`
  - retourne le template `exports/index`.

- `FinanceController`
  - gere la page `/admin/finance` ;
  - gere les routes d'export PDF ;
  - gere les routes d'export Excel ;
  - gere l'import Excel des ventes ;
  - retourne le template `finance/index`.

### Services

- `FinanceService`
  - calcule les recettes, depenses, benefice net, repartitions et couts par lot.

- `PdfExportService`
  - genere les PDF avec iText.

- `ExcelExportService`
  - genere les fichiers Excel avec Apache POI ;
  - importe les ventes depuis Excel.

### Templates

- `templates/finance/index.html`
  - tableau de bord financier.

- `templates/exports/index.html`
  - ecran central des exports PDF, exports Excel et import Excel.

- `templates/finance/test.html`, `templates/finance/vraitest.html`
  - pages de test finance.

- `templates/exports/test.html`, `templates/exports/vraitest.html`
  - pages de test export.

### Securite

Dans `SecurityConfig`, les routes suivantes sont limitees a `gestionnaire` ou `admin` :

```text
/admin/finance/**
/admin/exports
/admin/exports/**
```

## 4 - Autres remarques pour le fonctionnement du modules

- Les exports/imports utilisent les bibliotheques suivantes :
  - iText pour les PDF ;
  - Apache POI pour Excel ;
  - Thymeleaf pour les pages HTML.

- Les boutons Export / Import sont visibles dans le menu lateral via le lien `/admin/exports`.

- Certaines anciennes routes dans d'autres templates pointent encore vers `/finance/export/...` sans prefixe `/admin`. Les routes actuelles du controleur Finance utilisent `/admin/finance/...`. Si un bouton ne fonctionne pas dans une autre page, verifier d'abord cette difference d'URL.

- Le service `PdfExportService` contient une methode `generateRapportFinancePdf`, mais elle n'est pas encore implementee et retourne un tableau vide.

- Pour tester rapidement :
  - ouvrir `/admin/finance` pour verifier les indicateurs ;
  - ouvrir `/admin/exports` pour verifier les formulaires ;
  - lancer un export PDF ;
  - lancer un export Excel ;
  - importer un fichier `.xlsx` avec les colonnes attendues.

- Commande de verification du projet :

```bash
cd eggland
./mvnw test
```
