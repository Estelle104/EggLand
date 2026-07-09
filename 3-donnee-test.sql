-- 1. batiment (pas de dépendance)
INSERT INTO batiment (capacite, nom) VALUES
(100, 'Poulailler A'),
(200, 'Poulailler B');

-- 2. race (pas de dépendance)
INSERT INTO race (nom, prix_unitaire, rendement_moyen_mois) VALUES
('Sussex', 12000.00, 22),
('Harco', 13000.00, 24);

-- 3. lot (dépend de batiment et race)
INSERT INTO lot (age_semaine, date_arrivee, nombre_initial, batiment_id, race_id, statut) VALUES
(24, '2026-05-01', 90, 1, 1, 1),
(24, '2026-06-01', 100, 2, 1, 1);

INSERT INTO lot_races (nombre, lot_id, race_id) VALUES
(50, 1, 1),
(40, 1, 2),
(50, 2, 1),
(50, 2, 2);
-- 4. oeufproduction (dépend de lot)
INSERT INTO oeufproduction (date, quantite, lot_id) VALUES
('2026-05-31', 240, 1),
('2026-06-30', 280, 2),
('2026-06-30', 200, 1),
('2026-07-01', 85, 2),
('2026-07-09', 98, 2),
('2026-07-02', 65, 1),
('2026-07-02', 62, 2),
('2026-07-03', 70, 1),
('2026-07-03', 65, 2),
('2026-07-04', 65, 1),
('2026-07-04', 70, 2),
('2026-07-05', 70, 1),
('2026-07-05', 69, 2),
('2026-07-06', 68, 1),
('2026-07-06', 70, 2),
('2026-07-07', 60, 1),
('2026-07-07', 70, 2),
('2026-07-08', 70, 1),
('2026-07-08', 70, 2),
('2026-07-09', 62, 1);

-- 5. employe (pas de dépendance)
INSERT INTO employe (date_embauche, nom, prenom, salaire, tel) VALUES
('2026-04-24', 'Jean', 'Noah', 100000.00, '0326245985'),
('2026-04-24', 'Badoda', 'Zax', 120000.00, '0324598215');

-- 6. nourriture (pas de dépendance)
INSERT INTO nourriture (libelle, prix_unitaire, seuil_alerte) VALUES
('Mais', 2000.00, 50),
('Riz', 2300.00, 30),
('Herbe', 500.00, 5);

-- 7. mvtstock (dépend de lot et nourriture, lot_id peut être NULL)
INSERT INTO mvtstock (date, quantite, lot_id, nourriture_id, id_type) VALUES
('2026-05-01', 100.000, NULL, 1, 1),
('2026-05-01', 90.000, NULL, 2, 1),
('2026-05-01', 40.000, NULL, 3, 1),
('2026-05-08', 26.000, NULL, 1, 2),
('2026-05-08', 16.000, NULL, 2, 2),
('2026-05-08', 6.000, NULL, 3, 2),
('2026-05-15', 26.000, NULL, 1, 2),
('2026-05-15', 20.000, NULL, 2, 2),
('2026-05-15', 10.000, NULL, 3, 2);