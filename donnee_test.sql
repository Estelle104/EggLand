-- ============================================================
-- Donnees de test - EggLand
-- Compatible avec real_script_db.sql
-- ============================================================

TRUNCATE TABLE
    versementsalaire,
    paiementsalaire,
    notification,
    livraison,
    detailvente,
    vente,
    oeufstatut,
    oeufproduction,
    mvtstock,
    mvtargent,
    traitement,
    reforme,
    mort,
    lot_races,
    lot,
    client,
    useradmin,
    configuration,
    nourriture,
    employe,
    race,
    batiment,
    produitvente,
    roleuser,
    statutlivraison,
    statutvente,
    statutclient,
    typemvt,
    statutoeuf,
    typetraitement,
    statutlot
RESTART IDENTITY CASCADE;

-- ============================================================
-- 1. Tables de reference
-- ============================================================

INSERT INTO statutlot (id, code) VALUES
(1, 'actif'),
(2, 'reforme');

INSERT INTO typetraitement (id, code) VALUES
(1, 'vaccin'),
(2, 'maladie'),
(3, 'medicament');

INSERT INTO statutoeuf (id, code) VALUES
(1, 'vendu'),
(2, 'casse'),
(3, 'consomme'),
(4, 'valide');

INSERT INTO typemvt (id, code) VALUES
(1, 'entree'),
(2, 'sortie');

INSERT INTO statutclient (id, code) VALUES
(1, 'actif'),
(2, 'inactif'),
(3, 'en attente');

INSERT INTO statutvente (id, code) VALUES
(1, 'en_attente'),
(2, 'paye'),
(3, 'livre');

INSERT INTO produitvente (id, code) VALUES
(1, 'oeuf'),
(2, 'poule'),
(3, 'fumier');

INSERT INTO statutlivraison (id, code) VALUES
(1, 'en_attente'),
(2, 'en_cours'),
(3, 'livre');

INSERT INTO roleuser (id, code) VALUES
(1, 'admin'),
(2, 'gestionnaire');

-- ============================================================
-- 2. Tables principales
-- ============================================================

INSERT INTO race (id, nom, prix_unitaire, rendement_moyen_mois) VALUES
(1, 'Lohmann Brown', 12000.00, 28),
(2, 'Hy-Line Brown', 11500.00, 26),
(3, 'Isa Brown', 11000.00, 25),
(4, 'Cobb 500', 8000.00, 0),
(5, 'Ross 308', 7500.00, 0);

INSERT INTO batiment (id, nom, capacite) VALUES
(1, 'Batiment A - Pondeuses', 10000),
(2, 'Batiment B - Pondeuses', 8000),
(3, 'Batiment C - Elevage', 8000),
(4, 'Batiment D - Chair', 6000),
(5, 'Batiment E - Quarantaine', 3000);

INSERT INTO nourriture (id, libelle, prix_unitaire, seuil_alerte) VALUES
(1, 'Aliment pondeuse demarrage', 2500.00, 500),
(2, 'Aliment pondeuse croissance', 2300.00, 500),
(3, 'Aliment pondeuse ponte', 2800.00, 1000),
(4, 'Aliment poulet chair demarrage', 2200.00, 500),
(5, 'Aliment poulet chair finition', 2600.00, 500),
(6, 'Mais grain', 1500.00, 2000);

INSERT INTO employe (id, nom, prenom, tel, salaire, date_embauche) VALUES
(1, 'Rakoto', 'Jean', '+261 34 12 345 01', 450000.00, '2023-01-15'),
(2, 'Rabe', 'Marie', '+261 34 12 345 02', 420000.00, '2023-02-20'),
(3, 'Randria', 'Paul', '+261 34 12 345 03', 380000.00, '2023-03-10'),
(4, 'Rasoa', 'Lucienne', '+261 34 12 345 04', 400000.00, '2023-04-05'),
(5, 'Rakotoson', 'Faly', '+261 34 12 345 05', 350000.00, '2023-05-12');

INSERT INTO client (id, nom, prenom, tel, email, adresse, date_inscription, id_statut) VALUES
(1, 'Rakotoarimanana', 'Haja', '+261 32 11 111 01', 'haja.rakoto@email.com', 'Antananarivo', '2024-01-05', 1),
(2, 'Andrianjatovo', 'Nivo', '+261 33 22 222 02', 'nivo.andria@email.com', 'Antsirabe', '2024-02-10', 1),
(3, 'Razafindrakoto', 'Mamy', '+261 34 33 333 03', 'mamy.razaf@email.com', 'Fianarantsoa', '2024-03-15', 3),
(4, 'Rakotonirina', 'Lalao', '+261 32 44 444 04', 'lalao.rakoto@email.com', 'Toamasina', '2024-04-20', 1),
(5, 'Ravelo', 'Tahiry', '+261 33 55 555 05', 'tahiry.ravelo@email.com', 'Mahajanga', '2024-05-25', 2);

INSERT INTO useradmin (id, nom, email, mot_de_passe, role, actif) VALUES
(1, 'Admin Principal', 'admin@eggland.mg', 'admin123', 1, true),
(2, 'Gestionnaire Test', 'gestionnaire@eggland.mg', 'gestion123', 2, true);

INSERT INTO configuration (id, seuil_mort, seuil_nourriture) VALUES
(1, 5, 50.00);

-- ============================================================
-- 3. Lots et donnees d'elevage
-- ============================================================

INSERT INTO lot (id, race_id, date_arrivee, nombre_initial, age_semaine, statut, batiment_id) VALUES
(1, 1, '2025-01-10', 4800, 24, 1, 1),
(2, 2, '2025-02-15', 4500, 20, 1, 2),
(3, 3, '2025-03-01', 3800, 18, 1, 3),
(4, 4, '2025-05-05', 2800, 10, 1, 4),
(5, 5, '2025-06-01', 2500, 8, 1, 5),
(6, 1, '2024-08-15', 3000, 46, 2, 1),
(7, 2, '2024-09-20', 3000, 42, 2, 2);

INSERT INTO lot_races (id, lot_id, race_id, nombre) VALUES
(1, 1, 1, 4800),
(2, 2, 2, 4500),
(3, 3, 3, 3800),
(4, 4, 4, 2800),
(5, 5, 5, 2500),
(6, 6, 1, 3000),
(7, 7, 2, 3000);

INSERT INTO mort (id, lot_id, date, nombre) VALUES
(1, 1, '2025-01-15', 5),
(2, 1, '2025-01-20', 3),
(3, 2, '2025-02-20', 4),
(4, 2, '2025-03-05', 3),
(5, 3, '2025-03-10', 4),
(6, 4, '2025-05-10', 2),
(7, 5, '2025-06-05', 1),
(8, 6, '2024-09-01', 8),
(9, 7, '2024-10-01', 5);

INSERT INTO reforme (id, lot_id, date, nombre) VALUES
(1, 6, '2025-01-15', 3000),
(2, 7, '2025-01-20', 3000);

INSERT INTO traitement (id, lot_id, id_type, description, date, cout) VALUES
(1, 1, 1, 'Vaccination Newcastle', '2025-01-12', 120000.00),
(2, 1, 3, 'Supplement vitamine', '2025-02-01', 45000.00),
(3, 2, 1, 'Vaccination Gumboro', '2025-02-18', 110000.00),
(4, 3, 2, 'Traitement coccidiose', '2025-03-20', 78000.00),
(5, 4, 1, 'Vaccination Newcastle', '2025-05-08', 70000.00),
(6, 5, 1, 'Vaccination Gumboro', '2025-06-03', 62000.00);

INSERT INTO oeufproduction (id, lot_id, date, quantite) VALUES
(1, 1, '2025-01-20', 3200),
(2, 1, '2025-02-01', 3500),
(3, 1, '2025-02-15', 3800),
(4, 2, '2025-03-01', 3000),
(5, 2, '2025-03-15', 3200),
(6, 3, '2025-04-01', 2800),
(7, 3, '2025-04-15', 3000);

INSERT INTO oeufstatut (id, production_id, id_statut, quantite) VALUES
(1, 1, 4, 3000),
(2, 1, 2, 200),
(3, 2, 4, 3300),
(4, 2, 2, 200),
(5, 3, 4, 3600),
(6, 3, 2, 200),
(7, 4, 4, 2800),
(8, 4, 2, 200),
(9, 5, 4, 3000),
(10, 5, 2, 200),
(11, 6, 4, 2600),
(12, 6, 2, 200),
(13, 7, 4, 2800),
(14, 7, 2, 200);

-- ============================================================
-- 4. Ventes, livraisons et mouvements
-- ============================================================

INSERT INTO vente (id, client_id, date, total, id_statut) VALUES
(1, 1, '2025-02-10', 450000.00, 3),
(2, 2, '2025-02-15', 320000.00, 3),
(3, 3, '2025-03-05', 680000.00, 2),
(4, 4, '2025-03-20', 250000.00, 1),
(5, 5, '2025-04-10', 180000.00, 1);

INSERT INTO detailvente (id, vente_id, client_id, id_produit, quantite, prix_unitaire) VALUES
(1, 1, 1, 1, 1500.000, 300.00),
(2, 2, 2, 1, 1000.000, 320.00),
(3, 3, 3, 1, 2000.000, 340.00),
(4, 3, 3, 2, 50.000, 8000.00),
(5, 4, 4, 1, 800.000, 310.00),
(6, 5, 5, 3, 600.000, 300.00);

INSERT INTO livraison (id, vente_id, client_id, date_livraison, adresse_livraison, id_statut, frais_livraison) VALUES
(1, 1, 1, '2025-02-12', 'Lot IAV 1B, Antananarivo', 3, 25000.00),
(2, 2, 2, '2025-02-17', 'Route nationale 7, Antsirabe', 3, 35000.00),
(3, 3, 3, '2025-03-07', 'Avenue de l''Independance, Fianarantsoa', 2, 40000.00);

INSERT INTO mvtargent (id, id_type, montant, date, categorie, reference, lot_id) VALUES
(1, 1, 450000.00, '2025-02-10', 'vente', 'V-2025-001', NULL),
(2, 1, 320000.00, '2025-02-15', 'vente', 'V-2025-002', NULL),
(3, 2, 120000.00, '2025-01-12', 'traitement', 'TRT-001', 1),
(4, 2, 45000.00, '2025-02-01', 'traitement', 'TRT-002', 1),
(5, 2, 480000.00, '2025-03-01', 'achat nourriture', 'ACH-AL-001', NULL);

INSERT INTO mvtstock (id, nourriture_id, lot_id, id_type, quantite, date) VALUES
(1, 3, 1, 1, 2000.000, '2025-01-10'),
(2, 3, 1, 2, 500.000, '2025-01-15'),
(3, 3, 1, 2, 550.000, '2025-02-01'),
(4, 3, 2, 1, 1500.000, '2025-02-15'),
(5, 3, 2, 2, 400.000, '2025-02-20'),
(6, 1, 3, 2, 350.000, '2025-03-10'),
(7, 5, 4, 1, 1000.000, '2025-05-05'),
(8, 5, 4, 2, 300.000, '2025-05-10'),
(9, 5, 5, 1, 800.000, '2025-06-01'),
(10, 5, 5, 2, 250.000, '2025-06-05');

INSERT INTO notification (id, type, message, date_creation, lu) VALUES
(1, 'INFO', 'Bienvenue sur EggLand Management', '2025-01-01 08:00:00', true),
(2, 'WARNING', 'Seuil d''alerte aliment atteint pour le lot #1', '2025-02-15 09:30:00', false),
(3, 'INFO', 'Nouveau lot #5 enregistre', '2025-05-05 10:00:00', true);

INSERT INTO paiementsalaire (id, employe_id, mois, montant, paye, date_paiement, reference) VALUES
(1, 1, '2025-04-01', 450000.00, true, '2025-04-05', 'SAL-2025-04-001'),
(2, 2, '2025-04-01', 420000.00, true, '2025-04-05', 'SAL-2025-04-002'),
(3, 3, '2025-04-01', 380000.00, false, NULL, 'SAL-2025-04-003');

INSERT INTO versementsalaire (id, paiement_salaire_id, montant, date) VALUES
(1, 1, 450000.00, '2025-04-05'),
(2, 2, 420000.00, '2025-04-05');

-- ============================================================
-- 5. Recalage des sequences apres inserts explicites
-- ============================================================

SELECT setval(pg_get_serial_sequence('statutlot', 'id'), COALESCE(MAX(id), 1), true) FROM statutlot;
SELECT setval(pg_get_serial_sequence('typetraitement', 'id'), COALESCE(MAX(id), 1), true) FROM typetraitement;
SELECT setval(pg_get_serial_sequence('statutoeuf', 'id'), COALESCE(MAX(id), 1), true) FROM statutoeuf;
SELECT setval(pg_get_serial_sequence('typemvt', 'id'), COALESCE(MAX(id), 1), true) FROM typemvt;
SELECT setval(pg_get_serial_sequence('statutclient', 'id'), COALESCE(MAX(id), 1), true) FROM statutclient;
SELECT setval(pg_get_serial_sequence('statutvente', 'id'), COALESCE(MAX(id), 1), true) FROM statutvente;
SELECT setval(pg_get_serial_sequence('produitvente', 'id'), COALESCE(MAX(id), 1), true) FROM produitvente;
SELECT setval(pg_get_serial_sequence('statutlivraison', 'id'), COALESCE(MAX(id), 1), true) FROM statutlivraison;
SELECT setval(pg_get_serial_sequence('roleuser', 'id'), COALESCE(MAX(id), 1), true) FROM roleuser;
SELECT setval(pg_get_serial_sequence('race', 'id'), COALESCE(MAX(id), 1), true) FROM race;
SELECT setval(pg_get_serial_sequence('batiment', 'id'), COALESCE(MAX(id), 1), true) FROM batiment;
SELECT setval(pg_get_serial_sequence('lot', 'id'), COALESCE(MAX(id), 1), true) FROM lot;
SELECT setval(pg_get_serial_sequence('lot_races', 'id'), COALESCE(MAX(id), 1), true) FROM lot_races;
SELECT setval(pg_get_serial_sequence('mort', 'id'), COALESCE(MAX(id), 1), true) FROM mort;
SELECT setval(pg_get_serial_sequence('reforme', 'id'), COALESCE(MAX(id), 1), true) FROM reforme;
SELECT setval(pg_get_serial_sequence('traitement', 'id'), COALESCE(MAX(id), 1), true) FROM traitement;
SELECT setval(pg_get_serial_sequence('oeufproduction', 'id'), COALESCE(MAX(id), 1), true) FROM oeufproduction;
SELECT setval(pg_get_serial_sequence('oeufstatut', 'id'), COALESCE(MAX(id), 1), true) FROM oeufstatut;
SELECT setval(pg_get_serial_sequence('nourriture', 'id'), COALESCE(MAX(id), 1), true) FROM nourriture;
SELECT setval(pg_get_serial_sequence('mvtstock', 'id'), COALESCE(MAX(id), 1), true) FROM mvtstock;
SELECT setval(pg_get_serial_sequence('employe', 'id'), COALESCE(MAX(id), 1), true) FROM employe;
SELECT setval(pg_get_serial_sequence('mvtargent', 'id'), COALESCE(MAX(id), 1), true) FROM mvtargent;
SELECT setval(pg_get_serial_sequence('client', 'id'), COALESCE(MAX(id), 1), true) FROM client;
SELECT setval(pg_get_serial_sequence('vente', 'id'), COALESCE(MAX(id), 1), true) FROM vente;
SELECT setval(pg_get_serial_sequence('detailvente', 'id'), COALESCE(MAX(id), 1), true) FROM detailvente;
SELECT setval(pg_get_serial_sequence('livraison', 'id'), COALESCE(MAX(id), 1), true) FROM livraison;
SELECT setval(pg_get_serial_sequence('useradmin', 'id'), COALESCE(MAX(id), 1), true) FROM useradmin;
SELECT setval(pg_get_serial_sequence('configuration', 'id'), COALESCE(MAX(id), 1), true) FROM configuration;
SELECT setval(pg_get_serial_sequence('notification', 'id'), COALESCE(MAX(id), 1), true) FROM notification;
SELECT setval(pg_get_serial_sequence('paiementsalaire', 'id'), COALESCE(MAX(id), 1), true) FROM paiementsalaire;
SELECT setval(pg_get_serial_sequence('versementsalaire', 'id'), COALESCE(MAX(id), 1), true) FROM versementsalaire;
