-- 1. Batiment (pas de dépendance)
INSERT INTO Batiment (id, capacite, nom) VALUES
(1, 100, 'Poulailler A'),
(2, 200, 'Poulailler B');

-- 2. Race (pas de dépendance)
INSERT INTO race (id, nom, prix_unitaire, rendement_moyen_mois) VALUES
(1, 'Sussex', 12000.00, 22),
(2, 'Harco', 13000.00, 24);

-- 3. Lot (dépend de Batiment et Race)
INSERT INTO Lot (id, age_semaine, date_arrivee, nombre_initial, batiment_id, race_id, statut) VALUES
(1, 24, '2026-05-01', 90, 1, 1, 1),
(2, 24, '2026-06-01', 100, 2, 1, 1);

-- 4. OeufProduction (dépend de Lot)
INSERT INTO oeufproduction (id, date, quantite, lot_id) VALUES
(1, '2026-05-31', 240, 1),
(2, '2026-06-30', 280, 2),
(3, '2026-06-30', 200, 1),
(4, '2026-07-01', 85, 2),
(5, '2026-07-09', 98, 2),
(6, '2026-07-02', 65, 1),
(7, '2026-07-02', 62, 2),
(8, '2026-07-03', 70, 1),
(9, '2026-07-03', 65, 2),
(10, '2026-07-04', 65, 1),
(11, '2026-07-04', 70, 2),
(12, '2026-07-05', 70, 1),
(13, '2026-07-05', 69, 2),
(14, '2026-07-06', 68, 1),
(15, '2026-07-06', 70, 2),
(16, '2026-07-07', 60, 1),
(17, '2026-07-07', 70, 2),
(18, '2026-07-08', 70, 1),
(19, '2026-07-08', 70, 2),
(20, '2026-07-09', 62, 1);

-- 5. Employe (pas de dépendance)
INSERT INTO employe (id, date_embauche, nom, prenom, salaire, tel) VALUES
(1, '2026-04-24', 'Jean', 'Noah', 100000.00, '0326245985'),
(2, '2026-04-24', 'Badoda', 'Zax', 120000.00, '0324598215');

-- 6. Nourriture (pas de dépendance)
INSERT INTO nourriture (id, libelle, prix_unitaire, seuil_alerte) VALUES
(1, 'Mais', 2000.00, 50),
(2, 'Riz', 2300.00, 30),
(3, 'Herbe', 500.00, 5);

-- 7. MvtStock (dépend de Lot et Nourriture, lot_id peut être NULL)
INSERT INTO mvtStock (id, date, quantite, lot_id, nourriture_id, id_type) VALUES
(1, '2026-05-01', 100.000, NULL, 1, 1),
(2, '2026-05-01', 90.000, NULL, 2, 1),
(3, '2026-05-01', 40.000, NULL, 3, 1),
(4, '2026-05-08', 26.000, NULL, 1, 2),
(5, '2026-05-08', 16.000, NULL, 2, 2),
(6, '2026-05-08', 6.000, NULL, 3, 2),
(7, '2026-05-15', 26.000, NULL, 1, 2),
(8, '2026-05-15', 20.000, NULL, 2, 2),
(9, '2026-05-15', 10.000, NULL, 3, 2);