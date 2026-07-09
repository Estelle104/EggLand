INSERT INTO StatutLot (code) VALUES ('actif'), ('reforme');

INSERT INTO TypeTraitement (code) VALUES ('vaccin'), ('maladie'), ('medicament');

INSERT INTO StatutOeuf (code) VALUES ('vendu'), ('casse'), ('consomme'),('valide');

INSERT INTO TypeMvt (code) VALUES ('entree'), ('sortie');

INSERT INTO StatutClient (code) VALUES ('actif'), ('inactif'),('en attente');

INSERT INTO StatutVente (code) VALUES ('en_attente'), ('paye'), ('livre');

INSERT INTO ProduitVente (code) VALUES ('oeuf'), ('poule'), ('fumier');

INSERT INTO StatutLivraison (code) VALUES ('en_attente'), ('en_cours'), ('livre');

INSERT INTO RoleUser (code) VALUES ('admin'), ('gestionnaire');

INSERT INTO Configuration (seuil_mort, seuil_nourriture) VALUES (5, 50.0);

INSERT INTO useradmin(actif,email,mot_de_passe,nom,role) VALUES 
(true,'admin@gmail.com','admin123','moi',1),
(true,'gest@gmail.com','gestion123','ricardo',2);

INSERT INTO Client (nom, prenom, tel, email, adresse, date_inscription, id_statut) VALUES
('Rakoto', 'Jean', '0341234567', 'jean.rakoto@gmail.com', 'Lot II M 15 Antananarivo', '2026-01-10', 1);