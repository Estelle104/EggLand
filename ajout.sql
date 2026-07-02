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
