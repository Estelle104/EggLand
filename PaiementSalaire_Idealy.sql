CREATE TABLE PaiementSalaire (
    id              SERIAL PRIMARY KEY,
    employe_id      INT            NOT NULL REFERENCES Employe(id),
    mois            DATE           NOT NULL,      DECIMAL(12, 2) NOT NULL,
    paye            BOOLEAN        NOT NULL DEFAULT FALSE,
    date_paiement   DATE,
    reference       VARCHAR(150) UNIQUE,
    UNIQUE (employe_id, mois)
);


CREATE TABLE VersementSalaire (
    id                  SERIAL PRIMARY KEY,
    paiement_salaire_id INT            NOT NULL REFERENCES PaiementSalaire(id),
    montant             DECIMAL(12, 2) NOT NULL,
    date                DATE           NOT NULL
);