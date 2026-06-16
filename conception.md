## Race

| Champ | Type |
|---------|---------|
| id | PK |
| nom | VARCHAR |
| prix_unitaire | DECIMAL |
| rendement_moyen_mois | INT |

---

## Batiment

| Champ | Type |
|---------|---------|
| id | PK |
| nom | VARCHAR |
| capacite | INT |

---

## Lot

| Champ | Type |
|---------|---------|
| id | PK |
| race_id | FK → Race.id |
| date_arrivee | DATE |
| nombre_initial | INT |
| age_semaine | INT |
| statut | ENUM('actif', 'reforme') |
| batiment_id | FK → Batiment.id |

---

## Mort

| Champ | Type |
|---------|---------|
| id | PK |
| lot_id | FK → Lot.id |
| date | DATE |
| nombre | INT |

---

## Reforme

| Champ | Type |
|---------|---------|
| id | PK |
| lot_id | FK → Lot.id |
| date | DATE |
| nombre | INT |


---

## Traitement

| Champ | Type |
|---------|---------|
| id | PK |
| lot_id | FK → Lot.id |
| type | ENUM('vaccin', 'maladie', 'medicament') |
| description | TEXT |
| date | DATE |
| cout | DECIMAL |

---

## OeufProduction

| Champ | Type |
|---------|---------|
| id | PK |
| lot_id | FK → Lot.id |
| date | DATE |
| quantite | INT |

---

## OeufStatut

| Champ | Type |
|---------|---------|
| id | PK |
| production_id | FK → OeufProduction.id |
| statut | ENUM('vendu', 'casse', 'consomme') |
| quantite | INT |

---

## Nourriture

| Champ | Type |
|---------|---------|
| id | PK |
| libelle | VARCHAR |
| prix_unitaire | DECIMAL |
| seuil_alerte | INT |

---

## MvtStock

| Champ | Type |
|---------|---------|
| id | PK |
| nourriture_id | FK → nourriture.id |
| lot_id | FK → Lot.id (nullable) |
| type | ENUM('entree', 'sortie') |
| quantite | DECIMAL |
| date | DATE |

---

## Employe

| Champ | Type |
|---------|---------|
| id | PK |
| nom | VARCHAR |
| prenom | VARCHAR |
| tel | VARCHAR |
| salaire | DECIMAL |

---

## MvtArgent

| Champ | Type |
|---------|---------|
| id | PK |
| type | ENUM('entree', 'sortie') |
| montant | DECIMAL |
| date | DATE |
| categorie | VARCHAR |
| reference | VARCHAR |

### Exemples de catégories

- Aliment
- Vente œuf
- Vente poule
- Vente fumier
- Salaire
- Traitement
- Achat matériel
- Divers

---

## Client

| Champ | Type |
|---------|---------|
| id | PK |
| nom | VARCHAR |
| prenom | VARCHAR |
| tel | VARCHAR |
| email | VARCHAR |
| adresse | TEXT |
| date_inscription | DATE |
| statut | ENUM('actif', 'inactif') |

---

## Vente

| Champ | Type |
|---------|---------|
| id | PK |
| client_id | FK → Client.id |
| date | DATE |
| total | DECIMAL |
| statut | ENUM('en_attente', 'paye', 'livre') |

---

## DetailVente

| Champ | Type |
|---------|---------|
| id | PK |
| vente_id | FK → Vente.id |
| produit | ENUM('oeuf', 'poule', 'fumier') |
| quantite | DECIMAL |
| prix_unitaire | DECIMAL |

---

## Livraison

| Champ | Type |
|---------|---------|
| id | PK |
| vente_id | FK → Vente.id |
| client_id | FK → Client.id |
| date_livraison | DATE |
| adresse_livraison | TEXT |
| statut | ENUM('en_attente', 'en_cours', 'livre') |
| frais_livraison | DECIMAL |

---

## UserAdmin

| Champ | Type |
|---------|---------|
| id | PK |
| nom | VARCHAR |
| email | VARCHAR |
| mot_de_passe | VARCHAR |
| role | ENUM('admin', 'gestionnaire') |
| actif | BOOLEAN |

---


## Configuration 
| Champ | Type |
|---------|---------|
| id | PK |
| seuil_mort | INT |
| seuil_nourriture | DOUBLE |