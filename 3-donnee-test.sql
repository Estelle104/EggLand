-- 1. batiment (pas de dépendance)
INSERT INTO batiment (capacite, nom) VALUES
(100, 'Poulailler A'),
(200, 'Poulailler B');

-- 2. race (pas de dépendance)
INSERT INTO race (nom, prix_unitaire, rendement_moyen_mois) VALUES
('Sussex', 12000.00, 22),
('Harco', 13000.00, 24);


-- -- 5. employe (pas de dépendance)
INSERT INTO employe (date_embauche, nom, prenom, salaire, tel) VALUES
('2026-04-24', 'Jean', 'Noah', 100000.00, '0326245985'),
('2026-04-24', 'Badoda', 'Zax', 120000.00, '0324598215');

-- -- 6. nourriture (pas de dépendance)
INSERT INTO nourriture (libelle, prix_unitaire, seuil_alerte) VALUES
('Mais', 2000.00, 50),
('Riz', 2300.00, 30),
('Herbe', 500.00, 5);
