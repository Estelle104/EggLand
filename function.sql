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

CREATE VIEW v_lot_detail AS
SELECT
    l.id AS lot_id,
    l.nombre_initial,
    l.date_arrivee,
    l.age_semaine,

    r.id AS race_id,
    r.nom AS race_nom,

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
LEFT JOIN race r ON r.id = l.race_id
LEFT JOIN batiment b ON b.id = l.batiment_id
LEFT JOIN statutlot s ON s.id = l.statut
LEFT JOIN v_total_oeuf_lot o ON o.lot_id = l.id
LEFT JOIN v_total_mort_lot m ON m.lot_id = l.id
LEFT JOIN v_get_dernier_traitement_lot t ON t.lot_id = l.id;


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
