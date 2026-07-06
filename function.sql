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
