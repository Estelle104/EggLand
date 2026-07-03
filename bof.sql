ALTER TABLE lot_races ADD COLUMN nombre INT NOT NULL DEFAULT 0;

ALTER TABLE lot ALTER COLUMN race_id DROP NOT NULL;

CREATE OR REPLACE VIEW v_lot_detail AS
SELECT
    l.id AS lot_id,
    l.nombre_initial,
    l.date_arrivee,
    l.age_semaine,

    r.id AS race_id,
    r.nom AS race_nom,

    lr.id AS lot_race_id,
    lr.nombre,

    b.id AS batiment_id,
    b.nom AS batiment_nom,

    s.id AS statut_id,
    s.code AS statut_code,

    COALESCE(o.total_oeufs_produits, 0) AS total_oeufs_produits,

    COALESCE(m.total_morts, 0) AS total_morts,

    (l.nombre_initial - COALESCE(m.total_morts, 0)) AS effectif_restant,

    t.date_traitement,
    t.description AS dernier_traitement,
    t.cout AS cout_dernier_traitement,
    t.type_nom AS type_dernier_traitement

FROM lot l

LEFT JOIN lot_races lr
       ON lr.lot_id = l.id

LEFT JOIN race r
       ON r.id = lr.race_id

LEFT JOIN batiment b
       ON b.id = l.batiment_id

LEFT JOIN statutlot s
       ON s.id = l.statut

LEFT JOIN v_total_oeuf_lot o
       ON o.lot_id = l.id

LEFT JOIN v_total_mort_lot m
       ON m.lot_id = l.id

LEFT JOIN v_get_dernier_traitement_lot t
       ON t.lot_id = l.id;


-- ============================================================
-- Script de nettoyage et données de test — Eggland
-- ============================================================

-- Désactiver les contraintes FK (PostgreSQL)
SET session_replication_role = 'replica';

-- ============================================================
-- 1. VIDER TOUTES LES TABLES
-- ============================================================
TRUNCATE TABLE versementsalaire CASCADE;
TRUNCATE TABLE paiementsalaire CASCADE;
TRUNCATE TABLE detailvente CASCADE;
TRUNCATE TABLE livraison CASCADE;
TRUNCATE TABLE vente CASCADE;
TRUNCATE TABLE oeufstatut CASCADE;
TRUNCATE TABLE oeufproduction CASCADE;
TRUNCATE TABLE mvtstock CASCADE;
TRUNCATE TABLE mvtargent CASCADE;
TRUNCATE TABLE traitement CASCADE;
TRUNCATE TABLE reforme CASCADE;
TRUNCATE TABLE mort CASCADE;
TRUNCATE TABLE lot_races CASCADE;
TRUNCATE TABLE lot CASCADE;
TRUNCATE TABLE client CASCADE;
TRUNCATE TABLE useradmin CASCADE;
TRUNCATE TABLE notification CASCADE;
TRUNCATE TABLE configuration CASCADE;
TRUNCATE TABLE nourriture CASCADE;
TRUNCATE TABLE employe CASCADE;
TRUNCATE TABLE race CASCADE;
TRUNCATE TABLE batiment CASCADE;
TRUNCATE TABLE produitvente CASCADE;
TRUNCATE TABLE roleuser CASCADE;
TRUNCATE TABLE statutclient CASCADE;
TRUNCATE TABLE statutlivraison CASCADE;
TRUNCATE TABLE statutlot CASCADE;
TRUNCATE TABLE statutoeuf CASCADE;
TRUNCATE TABLE statutvente CASCADE;
TRUNCATE TABLE typemvt CASCADE;
TRUNCATE TABLE typetraitement CASCADE;

-- Réactiver les contraintes FK
SET session_replication_role = 'origin';

-- ============================================================
-- 2. TABLES DE RÉFÉRENCE (STATUTS, TYPES, ETC.)
-- ============================================================

-- statutclient
INSERT INTO statutclient (id, code) VALUES
(1, 'ACTIF'),
(2, 'INACTIF'),
(3, 'FIDELE');

-- statutlivraison
INSERT INTO statutlivraison (id, code) VALUES
(1, 'EN_ATTENTE'),
(2, 'EN_COURS'),
(3, 'LIVREE'),
(4, 'ANNULEE');

-- statutlot
INSERT INTO statutlot (id, code) VALUES
(1, 'ACTIF'),
(2, 'EN_REPRODUCTION'),
(3, 'REFORME'),
(4, 'TERMINE');

-- statutoeuf
INSERT INTO statutoeuf (id, code) VALUES
(1, 'COMMERCIAL'),
(2, 'INCUBE'),
(3, 'CASSE'),
(4, 'EN_STOCK');

-- statutvente
INSERT INTO statutvente (id, code) VALUES
(1, 'EN_ATTENTE'),
(2, 'CONFIRMEE'),
(3, 'LIVREE'),
(4, 'ANNULEE');

-- typemvt (mouvements argent & stock)
INSERT INTO typemvt (id, code) VALUES
(1, 'ENTREE'),
(2, 'SORTIE');

-- typetraitement
INSERT INTO typetraitement (id, code) VALUES
(1, 'VACCINATION'),
(2, 'VITAMINE'),
(3, 'TRAITEMENT_MALADIE'),
(4, 'DEVERMINAGE'),
(5, 'AUTRE');

-- roleuser
INSERT INTO roleuser (id, code) VALUES
(1, 'admin'),
(2, 'user'),
(3, 'superviseur');

-- produitvente
INSERT INTO produitvente (id, code) VALUES
(1, 'OEUF_COMMERCIAL'),
(2, 'OEUF_INCUBE'),
(3, 'POULET_VIVANT'),
(4, 'POULET_ABATTU'),
(5, 'FIENTE');

-- ============================================================
-- 3. TABLES CORE
-- ============================================================

-- batiment
INSERT INTO batiment (id, nom, capacite) VALUES
(1, 'Bâtiment A - Pondeuses', 5000),
(2, 'Bâtiment B - Poulets de chair', 3000),
(3, 'Bâtiment C - Élevage', 4000),
(4, 'Bâtiment D - Reproduction', 2000),
(5, 'Bâtiment E - Quarantaine', 1000);

-- race
INSERT INTO race (id, nom, prix_unitaire, rendement_moyen_mois) VALUES
(1, 'Lohmann Brown', 12000.00, 28),
(2, 'Hy-Line Brown', 11500.00, 26),
(3, 'Isa Brown', 11000.00, 25),
(4, 'Cobb 500', 8000.00, 0),
(5, 'Ross 308', 7500.00, 0);

-- employe
INSERT INTO employe (id, nom, prenom, tel, salaire, date_embauche) VALUES
(1, 'Rakoto', 'Jean', '+261 34 12 345 01', 450000.00, '2023-01-15'),
(2, 'Rabe', 'Marie', '+261 34 12 345 02', 420000.00, '2023-02-20'),
(3, 'Randria', 'Paul', '+261 34 12 345 03', 380000.00, '2023-03-10'),
(4, 'Rasoa', 'Lucienne', '+261 34 12 345 04', 400000.00, '2023-04-05'),
(5, 'Rakotoson', 'Faly', '+261 34 12 345 05', 350000.00, '2023-05-12'),
(6, 'Andriamparany', 'Tiana', '+261 34 12 345 06', 370000.00, '2023-06-01'),
(7, 'Ravelojaona', 'Hery', '+261 34 12 345 07', 430000.00, '2023-07-18'),
(8, 'Rajaonarison', 'Miora', '+261 34 12 345 08', 390000.00, '2024-01-10'),
(9, 'Rafanomezana', 'Soa', '+261 34 12 345 09', 360000.00, '2024-02-14'),
(10, 'Ralambo', 'Tafita', '+261 34 12 345 10', 410000.00, '2024-03-22');

-- nourriture
INSERT INTO nourriture (id, libelle, prix_unitaire, seuil_alerte) VALUES
(1, 'Aliment pondeuse démarrage', 2500.00, 500),
(2, 'Aliment pondeuse croissance', 2300.00, 500),
(3, 'Aliment pondeuse ponte', 2800.00, 1000),
(4, 'Aliment poulet chair démarrage', 2200.00, 500),
(5, 'Aliment poulet chair finition', 2600.00, 500),
(6, 'Maïs grain', 1500.00, 2000),
(7, 'Son de riz', 800.00, 1000),
(8, 'Farine de poisson', 3500.00, 300),
(9, 'Coquillage broyé', 1200.00, 400),
(10, 'Complément vitaminé', 5000.00, 200);

-- ============================================================
-- 4. TABLES DÉPENDANTES
-- ============================================================

-- lot (10 lots)
INSERT INTO lot (id, race_id, date_arrivee, nombre_initial, age_semaine, statut, batiment_id) VALUES
(1, 1, '2025-01-10', 4800, 24, 1, 1),
(2, 1, '2025-02-15', 4500, 20, 1, 1),
(3, 3, '2025-03-01', 3800, 18, 1, 3),
(4, 2, '2025-04-10', 4200, 14, 1, 2),
(5, 4, '2025-05-05', 2800, 10, 1, 4),
(6, 5, '2025-06-01', 2500, 8, 1, 4),
(7, 1, '2024-08-15', 5000, 46, 3, 1),
(8, 2, '2024-09-20', 4000, 42, 3, 2),
(9, 3, '2024-10-01', 3500, 40, 3, 3),
(10, 1, '2024-06-01', 4800, 56, 4, 1);

-- lot_races (associations supplémentaires)
INSERT INTO lot_races (lot_id, race_id, nombre) VALUES
(1, 1, 4800),
(2, 1, 4500),
(3, 3, 3800),
(4, 2, 4200),
(5, 4, 2800),
(6, 5, 2500);

-- client (10 clients)
INSERT INTO client (id, nom, prenom, tel, email, adresse, date_inscription, id_statut) VALUES
(1, 'Rakotoarimanana', 'Haja', '+261 32 11 111 01', 'haja.rakoto@email.com', 'Antananarivo', '2024-01-05', 1),
(2, 'Andrianjatovo', 'Nivo', '+261 33 22 222 02', 'nivo.andria@email.com', 'Antsirabe', '2024-02-10', 1),
(3, 'Razafindrakoto', 'Mamy', '+261 34 33 333 03', 'mamy.razaf@email.com', 'Fianarantsoa', '2024-03-15', 3),
(4, 'Rakotonirina', 'Lalao', '+261 32 44 444 04', 'lalao.rakoto@email.com', 'Toamasina', '2024-04-20', 1),
(5, 'Ravelo', 'Tahiry', '+261 33 55 555 05', 'tahiry.ravelo@email.com', 'Mahajanga', '2024-05-25', 2),
(6, 'Rasolofomanana', 'Herizo', '+261 34 66 666 06', 'herizo.rasolo@email.com', 'Toliara', '2024-06-30', 1),
(7, 'Rakotoson', 'Manoa', '+261 32 77 777 07', 'manoa.rakotoson@email.com', 'Antananarivo', '2024-07-05', 3),
(8, 'Rabeharisoa', 'Miora', '+261 33 88 888 08', 'miora.rabe@email.com', 'Antsiranana', '2024-08-10', 1),
(9, 'Randriamanana', 'Tolotra', '+261 34 99 999 09', 'tolotra.randria@email.com', 'Antananarivo', '2024-09-15', 2),
(10, 'Razafimahatratra', 'Sariaka', '+261 32 10 010 10', 'sariaka.razafy@email.com', 'Fianarantsoa', '2024-10-20', 1);

-- useradmin
INSERT INTO useradmin (id, nom, email, mot_de_passe, role, actif) VALUES
(1, 'Admin Principal', 'admin@eggland.mg', 'admin123', 1, true),
(2, 'User Test', 'user@eggland.mg', 'user123', 2, true),
(3, 'Superviseur', 'superviseur@eggland.mg', 'visor123', 3, true);

-- configuration (une seule ligne)
INSERT INTO configuration (id, seuil_mort, seuil_nourriture) VALUES
(1, 10, 500.00);

-- ============================================================
-- 5. DONNÉES DE TRANSACTIONS
-- ============================================================

-- mort (mortalité par lot)
INSERT INTO mort (id, lot_id, date, nombre) VALUES
(1, 1, '2025-01-15', 5),   (2, 1, '2025-01-20', 3),
(3, 1, '2025-02-01', 4),   (4, 1, '2025-02-15', 2),
(5, 1, '2025-03-01', 6),   (6, 2, '2025-02-20', 4),
(7, 2, '2025-03-05', 3),   (8, 2, '2025-03-20', 5),
(9, 2, '2025-04-01', 2),   (10, 3, '2025-03-10', 4),
(11, 3, '2025-03-25', 3),  (12, 3, '2025-04-10', 5),
(13, 4, '2025-04-15', 3),  (14, 4, '2025-05-01', 4),
(15, 4, '2025-05-15', 2),  (16, 5, '2025-05-10', 2),
(17, 5, '2025-05-25', 3),  (18, 6, '2025-06-05', 1),
(19, 7, '2024-09-01', 8),  (20, 7, '2024-10-01', 6),
(21, 7, '2024-11-01', 10), (22, 8, '2024-10-01', 5),
(23, 8, '2024-11-01', 7),  (24, 9, '2024-11-01', 4);

-- reforme
INSERT INTO reforme (id, lot_id, date, nombre) VALUES
(1, 7, '2025-01-15', 2000),
(2, 7, '2025-02-15', 1500),
(3, 8, '2025-01-20', 1800),
(4, 8, '2025-02-20', 1200),
(5, 9, '2025-01-25', 1500),
(6, 9, '2025-02-25', 1000),
(7, 10, '2025-01-10', 4800);

-- traitement
INSERT INTO traitement (id, lot_id, id_type, description, date, cout) VALUES
(1, 1, 1, 'Vaccination Newcastle', '2025-01-12', 120000.00),
(2, 1, 2, 'Supplément vitaminé eau de boisson', '2025-02-01', 45000.00),
(3, 1, 4, 'Déworming routine', '2025-02-15', 35000.00),
(4, 2, 1, 'Vaccination Gumboro', '2025-02-18', 110000.00),
(5, 2, 2, 'Complexe vitaminé', '2025-03-05', 42000.00),
(6, 3, 1, 'Vaccination Newcastle', '2025-03-05', 95000.00),
(7, 3, 3, 'Traitement coccidiose', '2025-03-20', 78000.00),
(8, 4, 1, 'Vaccination bronchite infectieuse', '2025-04-12', 105000.00),
(9, 4, 4, 'Déworming', '2025-04-25', 32000.00),
(10, 5, 1, 'Vaccination Newcastle', '2025-05-08', 70000.00),
(11, 6, 1, 'Vaccination Gumboro', '2025-06-03', 62000.00),
(12, 7, 1, 'Vaccination rappel', '2024-09-01', 130000.00),
(13, 8, 3, 'Traitement respiratoire', '2024-10-15', 95000.00),
(14, 9, 2, 'Vitamine boost', '2024-10-20', 38000.00),
(15, 10, 1, 'Vaccination complète', '2024-06-15', 150000.00);

-- oeufproduction (production d'oeufs — lots actifs)
INSERT INTO oeufproduction (id, lot_id, date, quantite) VALUES
(1, 1, '2025-01-20', 3200),  (2, 1, '2025-02-01', 3500),
(3, 1, '2025-02-15', 3800),  (4, 1, '2025-03-01', 4000),
(5, 1, '2025-03-15', 4100),  (6, 1, '2025-04-01', 3900),
(7, 1, '2025-04-15', 3800),  (8, 1, '2025-05-01', 3700),
(9, 1, '2025-05-15', 3600),  (10, 1, '2025-06-01', 3500),
(11, 2, '2025-03-01', 3000), (12, 2, '2025-03-15', 3200),
(13, 2, '2025-04-01', 3400), (14, 2, '2025-04-15', 3600),
(15, 2, '2025-05-01', 3700), (16, 2, '2025-05-15', 3800),
(17, 2, '2025-06-01', 3900), (18, 3, '2025-04-01', 2800),
(19, 3, '2025-04-15', 3000), (20, 3, '2025-05-01', 3100),
(21, 3, '2025-05-15', 3200), (22, 3, '2025-06-01', 3000),
(23, 4, '2025-05-01', 3500), (24, 4, '2025-05-15', 3700),
(25, 4, '2025-06-01', 3800);

-- oeufstatut (répartition des oeufs par statut)
INSERT INTO oeufstatut (id, production_id, id_statut, quantite) VALUES
(1, 1, 1, 3000),  (2, 1, 3, 200),   (3, 2, 1, 3300),  (4, 2, 3, 200),
(5, 3, 1, 3600),  (6, 3, 3, 200),   (7, 4, 1, 3800),  (8, 4, 3, 200),
(9, 5, 1, 3900),  (10, 5, 3, 200),  (11, 6, 1, 3700), (12, 6, 3, 200),
(13, 11, 1, 2800), (14, 11, 2, 200), (15, 13, 1, 3200), (16, 13, 2, 200),
(17, 18, 1, 2600), (18, 18, 3, 200), (19, 23, 1, 3300), (20, 23, 3, 200);

-- vente
INSERT INTO vente (id, client_id, date, total, id_statut) VALUES
(1, 1, '2025-02-10', 450000.00, 3),
(2, 2, '2025-02-15', 320000.00, 3),
(3, 3, '2025-03-05', 680000.00, 3),
(4, 4, '2025-03-20', 250000.00, 2),
(5, 1, '2025-04-01', 510000.00, 3),
(6, 5, '2025-04-10', 180000.00, 1),
(7, 6, '2025-04-20', 720000.00, 2),
(8, 7, '2025-05-05', 390000.00, 3),
(9, 3, '2025-05-15', 560000.00, 2),
(10, 8, '2025-05-25', 410000.00, 1),
(11, 2, '2025-06-01', 275000.00, 2),
(12, 9, '2025-06-05', 620000.00, 1),
(13, 10, '2025-06-10', 340000.00, 3),
(14, 4, '2025-06-15', 480000.00, 2),
(15, 7, '2025-06-20', 590000.00, 1);

-- detailvente
INSERT INTO detailvente (id, vente_id, client_id, id_produit, quantite, prix_unitaire) VALUES
(1, 1, 1, 1, 1500.000, 300.00),
(2, 2, 2, 1, 1000.000, 320.00),
(3, 3, 3, 1, 2000.000, 340.00),
(4, 3, 3, 4, 50.000, 8000.00),
(5, 4, 4, 1, 800.000, 310.00),
(6, 5, 1, 1, 1700.000, 300.00),
(7, 6, 5, 1, 600.000, 300.00),
(8, 7, 6, 1, 2200.000, 320.00),
(9, 7, 6, 4, 40.000, 7500.00),
(10, 8, 7, 1, 1300.000, 300.00),
(11, 9, 3, 1, 1800.000, 310.00),
(12, 10, 8, 1, 1400.000, 290.00),
(13, 11, 2, 1, 900.000, 305.00),
(14, 12, 9, 1, 2000.000, 310.00),
(15, 13, 10, 1, 1100.000, 310.00),
(16, 14, 4, 1, 1600.000, 300.00),
(17, 15, 7, 1, 1900.000, 310.00);

-- livraison
INSERT INTO livraison (id, vente_id, client_id, date_livraison, adresse_livraison, id_statut, frais_livraison) VALUES
(1, 1, 1, '2025-02-12', 'Lot IAV 1B, Antananarivo', 3, 25000.00),
(2, 2, 2, '2025-02-17', 'Route nationale 7, Antsirabe', 3, 35000.00),
(3, 3, 3, '2025-03-07', 'Avenue de l''Indépendance, Fianarantsoa', 3, 40000.00),
(4, 4, 4, '2025-03-22', 'Boulevard Ratsimilaho, Toamasina', 2, 30000.00),
(5, 5, 1, '2025-04-03', 'Lot IAV 1B, Antananarivo', 3, 25000.00),
(6, 8, 7, '2025-05-07', 'Anosy, Antananarivo', 3, 20000.00),
(7, 13, 10, '2025-06-12', 'Tsianolondroa, Fianarantsoa', 3, 35000.00);

-- mvtargent (entrées et sorties financières)
INSERT INTO mvtargent (id, id_type, montant, date, categorie, reference, lot_id) VALUES
(1, 1, 450000.00, '2025-02-10', 'VENTE_OEUF', 'V-2025-001', NULL),
(2, 1, 320000.00, '2025-02-15', 'VENTE_OEUF', 'V-2025-002', NULL),
(3, 2, 120000.00, '2025-01-12', 'VACCINATION', 'TRT-001', 1),
(4, 2, 45000.00, '2025-02-01', 'VITAMINE', 'TRT-002', 1),
(5, 2, 35000.00, '2025-02-15', 'DEVERMINAGE', 'TRT-003', 1),
(6, 1, 680000.00, '2025-03-05', 'VENTE_MIXTE', 'V-2025-003', NULL),
(7, 2, 110000.00, '2025-02-18', 'VACCINATION', 'TRT-004', 2),
(8, 2, 480000.00, '2025-03-01', 'ACHAT_ALIMENT', 'ACH-AL-001', NULL),
(9, 1, 250000.00, '2025-03-20', 'VENTE_OEUF', 'V-2025-004', NULL),
(10, 2, 95000.00, '2025-03-05', 'VACCINATION', 'TRT-006', 3),
(11, 2, 78000.00, '2025-03-20', 'TRAITEMENT', 'TRT-007', 3),
(12, 1, 510000.00, '2025-04-01', 'VENTE_OEUF', 'V-2025-005', NULL),
(13, 2, 320000.00, '2025-04-05', 'SALAIRE', 'SAL-2025-04', NULL),
(14, 2, 560000.00, '2025-04-10', 'ACHAT_ALIMENT', 'ACH-AL-002', NULL),
(15, 2, 350000.00, '2025-05-05', 'ACHAT_POUSSIER', 'ACH-POU-001', NULL),
(16, 1, 390000.00, '2025-05-05', 'VENTE_OEUF', 'V-2025-008', NULL),
(17, 1, 410000.00, '2025-05-25', 'VENTE_OEUF', 'V-2025-010', NULL),
(18, 2, 320000.00, '2025-05-28', 'SALAIRE', 'SAL-2025-05', NULL),
(19, 1, 620000.00, '2025-06-05', 'VENTE_OEUF', 'V-2025-012', NULL),
(20, 2, 340000.00, '2025-06-08', 'ACHAT_ALIMENT', 'ACH-AL-003', NULL),
(21, 1, 590000.00, '2025-06-20', 'VENTE_OEUF', 'V-2025-015', NULL),
(22, 2, 320000.00, '2025-06-25', 'SALAIRE', 'SAL-2025-06', NULL),
(23, 2, 150000.00, '2024-06-15', 'VACCINATION', 'TRT-015', 10),
(24, 2, 130000.00, '2024-09-01', 'VACCINATION', 'TRT-012', 7);

-- mvtstock (mouvements de nourriture)
INSERT INTO mvtstock (id, nourriture_id, lot_id, id_type, quantite, date) VALUES
(1, 3, 1, 2, 500.000, '2025-01-15'),
(2, 3, 1, 2, 550.000, '2025-02-01'),
(3, 3, 1, 2, 600.000, '2025-02-15'),
(4, 3, 2, 2, 400.000, '2025-02-20'),
(5, 3, 2, 2, 450.000, '2025-03-05'),
(6, 5, 5, 2, 300.000, '2025-05-10'),
(7, 5, 5, 2, 350.000, '2025-05-25'),
(8, 5, 6, 2, 250.000, '2025-06-05'),
(9, 3, 1, 2, 600.000, '2025-03-01'),
(10, 3, 1, 2, 550.000, '2025-03-15'),
(11, 3, 2, 2, 450.000, '2025-04-01'),
(12, 3, 3, 2, 400.000, '2025-04-01'),
(13, 3, 4, 2, 450.000, '2025-05-01'),
(14, 1, 3, 2, 350.000, '2025-03-10'),
(15, 1, 4, 2, 300.000, '2025-04-15'),
(16, 3, 1, 1, 2000.000, '2025-01-10'),
(17, 5, 5, 1, 1000.000, '2025-05-05'),
(18, 5, 6, 1, 800.000, '2025-06-01'),
(19, 3, 2, 1, 1500.000, '2025-02-15'),
(20, 3, 4, 1, 1200.000, '2025-04-10');

-- paiementsalaire (bulletins de salaire)
INSERT INTO paiementsalaire (id, employe_id, mois, montant, paye, date_paiement, reference) VALUES
(1, 1, '2025-04-01', 450000.00, true, '2025-04-05', 'SAL-2025-04-001'),
(2, 2, '2025-04-01', 420000.00, true, '2025-04-05', 'SAL-2025-04-002'),
(3, 3, '2025-04-01', 380000.00, true, '2025-04-05', 'SAL-2025-04-003'),
(4, 4, '2025-04-01', 400000.00, true, '2025-04-05', 'SAL-2025-04-004'),
(5, 5, '2025-04-01', 350000.00, true, '2025-04-05', 'SAL-2025-04-005'),
(6, 1, '2025-05-01', 450000.00, true, '2025-05-02', 'SAL-2025-05-001'),
(7, 2, '2025-05-01', 420000.00, true, '2025-05-02', 'SAL-2025-05-002'),
(8, 3, '2025-05-01', 380000.00, true, '2025-05-02', 'SAL-2025-05-003'),
(9, 4, '2025-05-01', 400000.00, true, '2025-05-02', 'SAL-2025-05-004'),
(10, 5, '2025-05-01', 350000.00, true, '2025-05-02', 'SAL-2025-05-005'),
(11, 6, '2025-05-01', 370000.00, true, '2025-05-02', 'SAL-2025-05-006'),
(12, 7, '2025-05-01', 430000.00, true, '2025-05-02', 'SAL-2025-05-007');

-- versementsalaire (versements effectifs)
INSERT INTO versementsalaire (id, paiement_salaire_id, montant, date) VALUES
(1, 1, 450000.00, '2025-04-05'),
(2, 2, 420000.00, '2025-04-05'),
(3, 3, 380000.00, '2025-04-05'),
(4, 4, 400000.00, '2025-04-05'),
(5, 5, 350000.00, '2025-04-05'),
(6, 6, 450000.00, '2025-05-02'),
(7, 7, 420000.00, '2025-05-02'),
(8, 8, 380000.00, '2025-05-02'),
(9, 9, 400000.00, '2025-05-02'),
(10, 10, 350000.00, '2025-05-02'),
(11, 11, 370000.00, '2025-05-02'),
(12, 12, 430000.00, '2025-05-02');

-- notification
INSERT INTO notification (id, type, message, date_creation, lu) VALUES
(1, 'INFO', 'Bienvenue sur Eggland Management', '2025-01-01 08:00:00', true),
(2, 'WARNING', 'Seuil d''alerte aliment atteint pour le lot #1', '2025-02-15 09:30:00', false),
(3, 'INFO', 'Nouveau lot #5 enregistré', '2025-05-05 10:00:00', true),
(4, 'DANGER', 'Taux de mortalité élevé détecté sur le lot #7', '2024-10-15 14:00:00', true),
(5, 'INFO', 'Production mensuelle : 14500 oeufs en avril', '2025-05-01 08:00:00', false),
(6, 'WARNING', 'Stock de maïs faible (< 2000 kg)', '2025-06-10 11:00:00', false),
(7, 'INFO', 'Paiement des salaires effectué', '2025-05-02 16:00:00', true),
(8, 'SUCCESS', 'Vente #8 confirmée et livrée', '2025-05-07 09:00:00', true);

CREATE OR REPLACE VIEW v_get_dernier_traitement_lot AS
SELECT DISTINCT ON (t.lot_id)
    t.lot_id,
    t.date AS date_traitement,
    t.description,
    t.cout,
    tt.code AS type_nom
FROM traitement t
LEFT JOIN typetraitement tt ON tt.id = t.id_type
ORDER BY t.lot_id, t.date DESC;

CREATE OR REPLACE VIEW v_total_oeuf_lot AS
SELECT
    l.id AS lot_id,
    COALESCE(SUM(op.quantite), 0) AS total_oeufs_produits
FROM lot l
LEFT JOIN oeufproduction op
    ON op.lot_id = l.id
GROUP BY l.id;

CREATE OR REPLACE VIEW v_total_mort_lot AS
SELECT
    l.id AS lot_id,
    COALESCE(SUM(m.nombre), 0) AS total_morts
FROM lot l
LEFT JOIN mort m
    ON m.lot_id = l.id
GROUP BY l.id;

CREATE OR REPLACE VIEW v_total_reforme_lot AS
SELECT
    l.id AS lot_id,
    COALESCE(SUM(rf.nombre), 0) AS total_reforme
FROM lot l
LEFT JOIN reforme rf
    ON rf.lot_id = l.id
GROUP BY l.id;


CREATE OR REPLACE VIEW v_historique_production AS
SELECT 
    ROW_NUMBER() OVER (ORDER BY op.date DESC, op.lot_id, so.code DESC) AS id,
    op.id AS production_id,
    op.date,
    op.lot_id,
    so.code AS statut,
    os.quantite
FROM OeufStatut os
JOIN StatutOeuf so ON so.id = os.id_statut
JOIN OeufProduction op ON os.production_id = op.id
ORDER BY op.date DESC, op.lot_id, so.code DESC;
