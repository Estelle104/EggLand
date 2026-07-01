
-- TABLES DE REFERENCE (statuts / types)

CREATE TABLE statutlot (
    id   SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE statutclient (
    id   SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE statutoeuf (
    id   SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE statutvente (
    id   SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE statutlivraison (
    id   SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE typemvt (
    id   SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE typetraitement (
    id   SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE produitvente (
    id   SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE roleuser (
    id   SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE
);

-- TABLES PRINCIPALES

CREATE TABLE race (
    id                    SERIAL PRIMARY KEY,
    nom                   VARCHAR(100)   NOT NULL,
    prix_unitaire         NUMERIC(12,2)  NOT NULL,
    rendement_moyen_mois  INTEGER        NOT NULL
);

CREATE TABLE batiment (
    id       SERIAL PRIMARY KEY,
    nom      VARCHAR(100) NOT NULL,
    capacite INTEGER      NOT NULL
);

CREATE TABLE nourriture (
    id            SERIAL PRIMARY KEY,
    libelle       VARCHAR(150)  NOT NULL,
    prix_unitaire NUMERIC(12,2) NOT NULL,
    seuil_alerte  INTEGER
);

CREATE TABLE employe (
    id             SERIAL PRIMARY KEY,
    nom            VARCHAR(100)  NOT NULL,
    prenom         VARCHAR(100)  NOT NULL,
    tel            VARCHAR(20),
    salaire        NUMERIC(12,2) NOT NULL,
    date_embauche  DATE          NOT NULL
);

CREATE TABLE client (
    id                SERIAL PRIMARY KEY,
    nom               VARCHAR(100) NOT NULL,
    prenom            VARCHAR(100),
    tel               VARCHAR(20),
    email             VARCHAR(150),
    adresse           TEXT,
    date_inscription  DATE         NOT NULL,
    id_statut         INTEGER      NOT NULL REFERENCES statutclient(id)
);

-- NOTE: colonne "statut" (et non "id_statut") car c'est le nom
-- déclaré dans l'entité Lot.java -> @JoinColumn(name = "statut")
CREATE TABLE lot (
    id              SERIAL PRIMARY KEY,
    race_id         INTEGER      NOT NULL REFERENCES race(id), -- NOT NULL ici, mais un ALTER TABLE précédent le rendait nullable en base réelle : à harmoniser
    date_arrivee    DATE         NOT NULL,
    nombre_initial  INTEGER      NOT NULL,
    age_semaine     INTEGER      NOT NULL,
    statut          INTEGER      NOT NULL REFERENCES statutlot(id), -- nom incohérent avec le reste du schéma (ailleurs c'est "id_statut")
    batiment_id     INTEGER      NOT NULL REFERENCES batiment(id)
);

-- Table de répartition Lot <-> Race (relation @OneToMany dans Lot.java)
CREATE TABLE lot_races (
    id      SERIAL PRIMARY KEY,
    lot_id  INTEGER REFERENCES lot(id),
    race_id INTEGER REFERENCES race(id),
    nombre  INTEGER
);

CREATE TABLE mort (
    id     SERIAL PRIMARY KEY,
    lot_id INTEGER NOT NULL REFERENCES lot(id),
    date   DATE    NOT NULL,
    nombre INTEGER NOT NULL
);

CREATE TABLE reforme (
    id     SERIAL PRIMARY KEY,
    lot_id INTEGER NOT NULL REFERENCES lot(id),
    date   DATE    NOT NULL,
    nombre INTEGER NOT NULL
);

CREATE TABLE traitement (
    id           SERIAL PRIMARY KEY,
    lot_id       INTEGER       NOT NULL REFERENCES lot(id),
    id_type      INTEGER       NOT NULL REFERENCES typetraitement(id),
    description  TEXT,
    date         DATE          NOT NULL,
    cout         NUMERIC(10,2) NOT NULL
);

CREATE TABLE oeufproduction (
    id       SERIAL PRIMARY KEY,
    lot_id   INTEGER NOT NULL REFERENCES lot(id),
    date     DATE    NOT NULL,
    quantite INTEGER NOT NULL
);

CREATE TABLE oeufstatut (
    id             SERIAL PRIMARY KEY,
    production_id  INTEGER NOT NULL REFERENCES oeufproduction(id),
    id_statut      INTEGER NOT NULL REFERENCES statutoeuf(id),
    quantite       INTEGER NOT NULL
);

CREATE TABLE mvtstock (
    id             SERIAL PRIMARY KEY,
    nourriture_id  INTEGER       NOT NULL REFERENCES nourriture(id),
    lot_id         INTEGER       REFERENCES lot(id),
    id_type        INTEGER       NOT NULL REFERENCES typemvt(id),
    quantite       NUMERIC(12,3) NOT NULL,
    date           DATE          NOT NULL
);

CREATE TABLE mvtargent (
    id         SERIAL PRIMARY KEY,
    id_type    INTEGER       NOT NULL REFERENCES typemvt(id),
    montant    NUMERIC(12,2) NOT NULL,
    date       DATE          NOT NULL,
    categorie  VARCHAR(100),
    reference  VARCHAR(150) UNIQUE,
    lot_id     INTEGER       REFERENCES lot(id)
);

CREATE TABLE vente (
    id         SERIAL PRIMARY KEY,
    client_id  INTEGER       NOT NULL REFERENCES client(id),
    date       DATE          NOT NULL,
    total      NUMERIC(12,2) NOT NULL,
    id_statut  INTEGER       NOT NULL REFERENCES statutvente(id)
);

-- NOTE: correspond à l'entité DetailVente.java telle qu'écrite
-- (client_id et id_produit y sont référencés ; à créer si absents en base réelle)
CREATE TABLE detailvente (
    id             SERIAL PRIMARY KEY,
    vente_id       INTEGER        NOT NULL REFERENCES vente(id),
    client_id      INTEGER        NOT NULL REFERENCES client(id),
    id_produit     INTEGER        NOT NULL REFERENCES produitvente(id),
    quantite       NUMERIC(10,3)  NOT NULL,
    prix_unitaire  NUMERIC(10,2)  NOT NULL
);

CREATE TABLE livraison (
    id                 SERIAL PRIMARY KEY,
    vente_id           INTEGER       NOT NULL REFERENCES vente(id),
    client_id          INTEGER       NOT NULL REFERENCES client(id),
    date_livraison     DATE          NOT NULL,
    adresse_livraison  TEXT,
    id_statut          INTEGER       NOT NULL REFERENCES statutlivraison(id),
    frais_livraison    NUMERIC(12,2) NOT NULL
);

CREATE TABLE useradmin (
    id            SERIAL PRIMARY KEY,
    nom           VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe  VARCHAR(255) NOT NULL,
    role          INTEGER      NOT NULL REFERENCES roleuser(id),
    actif         BOOLEAN      NOT NULL
);

CREATE TABLE configuration (
    id                SERIAL PRIMARY KEY,
    seuil_mort        INTEGER          NOT NULL,
    seuil_nourriture  DOUBLE PRECISION NOT NULL
);

CREATE TABLE notification (
    id             SERIAL PRIMARY KEY,
    type           VARCHAR(50) NOT NULL,
    message        TEXT,
    date_creation  TIMESTAMP   NOT NULL,
    lu             BOOLEAN     NOT NULL
);

CREATE TABLE paiementsalaire (
    id             SERIAL PRIMARY KEY,
    employe_id     INTEGER       NOT NULL REFERENCES employe(id),
    mois           DATE          NOT NULL, -- toujours le 1er jour du mois, ex: 2026-06-01
    montant        NUMERIC(12,2) NOT NULL,
    paye           BOOLEAN       NOT NULL DEFAULT FALSE,
    date_paiement  DATE,
    reference      VARCHAR(150) UNIQUE
);

CREATE TABLE versementsalaire (
    id                    SERIAL PRIMARY KEY,
    paiement_salaire_id   INTEGER       NOT NULL REFERENCES paiementsalaire(id),
    montant               NUMERIC(12,2) NOT NULL,
    date                  DATE          NOT NULL
);