CREATE DATABASE eggland_db;

CREATE TABLE StatutLot (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) 
);

-- ---

CREATE TABLE TypeTraitement (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) 
);

-- ---

CREATE TABLE StatutOeuf (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) 
);

-- ---

CREATE TABLE TypeMvt (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) 
);

-- ---

CREATE TABLE StatutClient (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) 
);

-- ---

CREATE TABLE StatutVente (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) 
);

-- ---

CREATE TABLE ProduitVente (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) 
);

-- ---

CREATE TABLE StatutLivraison (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) 
);

-- ---

CREATE TABLE RoleUser (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) 
);


-- ============================================================
-- TABLES PRINCIPALES
-- ============================================================

CREATE TABLE Race (
    id                   SERIAL PRIMARY KEY,
    nom                  VARCHAR(100)   NOT NULL,
    prix_unitaire        DECIMAL(10, 2) NOT NULL,
    rendement_moyen_mois INT            NOT NULL
);

-- ---

CREATE TABLE Batiment (
    id       SERIAL PRIMARY KEY,
    nom      VARCHAR(100) NOT NULL,
    capacite INT          NOT NULL
);

-- ---

CREATE TABLE Lot (
    id             SERIAL PRIMARY KEY,
    race_id        INT          NOT NULL REFERENCES Race(id),
    date_arrivee   DATE         NOT NULL,
    nombre_initial INT          NOT NULL,
    age_semaine    INT          NOT NULL,
    id_statut         INT          NOT NULL DEFAULT 1 REFERENCES StatutLot(id),
    batiment_id    INT          NOT NULL REFERENCES Batiment(id)
);

-- ---

CREATE TABLE Mort (
    id     SERIAL PRIMARY KEY,
    lot_id INT  NOT NULL REFERENCES Lot(id),
    date   DATE NOT NULL,
    nombre INT  NOT NULL
);

-- ---

CREATE TABLE Reforme (
    id     SERIAL PRIMARY KEY,
    lot_id INT  NOT NULL REFERENCES Lot(id),
    date   DATE NOT NULL,
    nombre INT  NOT NULL
);

-- ---

CREATE TABLE Traitement (
    id          SERIAL PRIMARY KEY,
    lot_id      INT           NOT NULL REFERENCES Lot(id),
    id_type        INT           NOT NULL REFERENCES TypeTraitement(id),
    description TEXT,
    date        DATE          NOT NULL,
    cout        DECIMAL(10,2) NOT NULL DEFAULT 0
);

-- ---

CREATE TABLE OeufProduction (
    id       SERIAL PRIMARY KEY,
    lot_id   INT  NOT NULL REFERENCES Lot(id),
    date     DATE NOT NULL,
    quantite INT  NOT NULL
);

-- ---

CREATE TABLE OeufStatut (
    id            SERIAL PRIMARY KEY,
    production_id INT         NOT NULL REFERENCES OeufProduction(id),
    id_statut        INT         NOT NULL REFERENCES StatutOeuf(id),
    quantite      INT         NOT NULL
);

-- ---

CREATE TABLE Nourriture (
    id            SERIAL PRIMARY KEY,
    libelle       VARCHAR(150)   NOT NULL,
    prix_unitaire DECIMAL(10, 2) NOT NULL,
    seuil_alerte  INT            NOT NULL DEFAULT 0
);

-- ---

CREATE TABLE MvtStock (
    id            SERIAL PRIMARY KEY,
    nourriture_id INT           NOT NULL REFERENCES Nourriture(id),
    lot_id        INT           REFERENCES Lot(id),
    id_type          INT           NOT NULL REFERENCES TypeMvt(id),
    quantite      DECIMAL(10,3) NOT NULL,
    date          DATE          NOT NULL
);

-- ---

CREATE TABLE Employe (
    id      SERIAL PRIMARY KEY,
    nom     VARCHAR(100)   NOT NULL,
    prenom  VARCHAR(100)   NOT NULL,
    tel     VARCHAR(20),
    salaire DECIMAL(10, 2) NOT NULL DEFAULT 0
);

-- ---

CREATE TABLE MvtArgent (
    id        SERIAL PRIMARY KEY,
    id_type          INT           NOT NULL REFERENCES TypeMvt(id),
    montant   DECIMAL(12, 2) NOT NULL,
    date      DATE           NOT NULL,
    categorie VARCHAR(100),
    reference VARCHAR(150) UNIQUE
);

-- ---

CREATE TABLE Client (
    id               SERIAL PRIMARY KEY,
    nom              VARCHAR(100) NOT NULL,
    prenom           VARCHAR(100),
    tel              VARCHAR(20),
    email            VARCHAR(150),
    adresse          TEXT,
    date_inscription DATE         NOT NULL DEFAULT CURRENT_DATE,
    id_statut           INT          NOT NULL DEFAULT 1 REFERENCES StatutClient(id)
);

-- ---

CREATE TABLE Vente (
    id        SERIAL PRIMARY KEY,
    client_id INT           NOT NULL REFERENCES Client(id),
    date      DATE          NOT NULL DEFAULT CURRENT_DATE,
    total     DECIMAL(12,2) NOT NULL DEFAULT 0,
    id_statut    INT   NOT NULL DEFAULT 1 REFERENCES StatutVente(id)
);

-- ---

CREATE TABLE DetailVente (
    id            SERIAL PRIMARY KEY,
    vente_id      INT           NOT NULL REFERENCES Vente(id),
    produit       VARCHAR(30)   NOT NULL REFERENCES ProduitVente(code),
    quantite      DECIMAL(10,3) NOT NULL,
    prix_unitaire DECIMAL(10,2) NOT NULL
);

-- ---

CREATE TABLE Livraison (
    id                SERIAL PRIMARY KEY,
    vente_id          INT           NOT NULL REFERENCES Vente(id),
    client_id         INT           NOT NULL REFERENCES Client(id),
    date_livraison    DATE          NOT NULL,
    adresse_livraison TEXT,
    id_statut            INT           NOT NULL DEFAULT 1 REFERENCES StatutLivraison(id),
    frais_livraison   DECIMAL(10,2) NOT NULL DEFAULT 0
);

-- ---

CREATE TABLE UserAdmin (
    id           SERIAL PRIMARY KEY,
    nom          VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role         INT          NOT NULL REFERENCES RoleUser(id),
    actif        BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ---

CREATE TABLE Configuration (
    id               SERIAL PRIMARY KEY,
    seuil_mort       INT              NOT NULL DEFAULT 0,
    seuil_nourriture DOUBLE PRECISION NOT NULL DEFAULT 0
);

INSERT INTO StatutLot (code) VALUES ('actif'), ('reforme');
INSERT INTO TypeTraitement (code) VALUES ('vaccin'), ('maladie'), ('medicament');
INSERT INTO StatutOeuf (code) VALUES ('vendu'), ('casse'), ('consomme');
INSERT INTO TypeMvt (code) VALUES ('entree'), ('sortie');
INSERT INTO StatutClient (code) VALUES ('actif'), ('inactif');
INSERT INTO StatutVente (code) VALUES ('en_attente'), ('paye'), ('livre');
INSERT INTO ProduitVente (code) VALUES ('oeuf'), ('poule'), ('fumier');
INSERT INTO StatutLivraison (code) VALUES ('en_attente'), ('en_cours'), ('livre');
INSERT INTO RoleUser (code) VALUES ('admin'), ('gestionnaire');
INSERT INTO Configuration (seuil_mort, seuil_nourriture) VALUES (5, 50.0);
INSERT INTO useradmin(actif,email,mot_de_passe,nom,role) VALUES 
(true,'admin@gmail.com','admin123','moi',1),
(true,'gest@gmail.com','gestion123','ricardo',2);
INSERT INTO Nourriture (libelle, prix_unitaire, seuil_alerte) VALUES
('Maïs', 2000.00, 50),
('Soja', 2500.00, 30),
('Farine', 1500.00, 40);