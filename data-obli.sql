
INSERT INTO ProduitVente (code) VALUES ('oeuf'), ('poule'), ('fumier');
INSERT INTO StatutVente (code) VALUES ('en_attente'), ('paye');
INSERT INTO TypeMvt (code) VALUES ('entree'), ('sortie');
INSERT INTO StatutLot (code) VALUES ('actif'), ('reforme');
INSERT INTO StatutOeuf (code) VALUES ('vendu'), ('casse'), ('consomme');
INSERT INTO TypeTraitement (code) VALUES ('vaccin'), ('maladie'), ('medicament');
INSERT INTO StatutClient (code) VALUES ('actif'), ('inactif');
INSERT INTO StatutLivraison (code) VALUES ('en_attente'), ('en_cours'), ('livre');
INSERT INTO RoleUser (code) VALUES ('admin'), ('gestionnaire');
INSERT INTO Configuration (seuil_mort, seuil_nourriture) VALUES (50, 50.0);