-- Vue dernier traitement par lot
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


-- Vue total oeufs produits par lot
CREATE OR REPLACE VIEW v_total_oeuf_lot AS
SELECT
    l.id AS lot_id,
    COALESCE(SUM(op.quantite), 0) AS total_oeufs_produits
FROM lot l
LEFT JOIN oeufproduction op
    ON op.lot_id = l.id
GROUP BY l.id;


-- Vue total morts par lot
CREATE OR REPLACE VIEW v_total_mort_lot AS
SELECT
    l.id AS lot_id,
    COALESCE(SUM(m.nombre), 0) AS total_morts
FROM lot l
LEFT JOIN mort m
    ON m.lot_id = l.id
GROUP BY l.id;


-- Vue total reforme par lot
CREATE OR REPLACE VIEW v_total_reforme_lot AS
SELECT
    l.id AS lot_id,
    COALESCE(SUM(rf.nombre), 0) AS total_reforme
FROM lot l
LEFT JOIN reforme rf
    ON rf.lot_id = l.id
GROUP BY l.id;


-- Vue historique production
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


-- changement lot_race
ALTER TABLE lot_races ADD COLUMN nombre INT NOT NULL DEFAULT 0;


-- changement lot
ALTER TABLE lot ALTER COLUMN race_id DROP NOT NULL;


-- vue detail lot
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


CREATE OR REPLACE VIEW historique_vente_poule AS
WITH ventes_poule AS (
    SELECT dv.*
    FROM DetailVente dv
    JOIN ProduitVente pv ON pv.id = dv.id_produit
    WHERE pv.code = 'poule'
),
totaux_vendus AS (
    SELECT lot_id, race_id, SUM(quantite) AS total_vendu
    FROM ventes_poule
    GROUP BY lot_id, race_id
)
SELECT
    vp.id                       AS detailvente_id,
    vp.vente_id                 AS vente_id,
    v.date                      AS date_vente,
    vp.lot_id                   AS lot_id,
    vp.race_id                  AS race_id,
    r.nom                       AS race_nom,
    sl.code                     AS statut_lot,      -- statut du lot, non modifié
    vp.quantite                 AS nombre_vendu,
    (lr.nombre + t.total_vendu) AS nombre_avant_ventes,
    (lr.nombre + t.total_vendu) - SUM(vp.quantite) OVER (
        PARTITION BY vp.lot_id, vp.race_id
        ORDER BY v.date, vp.id
        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS nombre_restant_apres_vente
FROM ventes_poule vp
JOIN Vente v         ON v.id = vp.vente_id
JOIN Lot l           ON l.id = vp.lot_id
JOIN Race r          ON r.id = vp.race_id
JOIN StatutLot sl    ON sl.id = l.statut
JOIN lot_races lr    ON lr.lot_id = vp.lot_id AND lr.race_id = vp.race_id
JOIN totaux_vendus t ON t.lot_id = vp.lot_id AND t.race_id = vp.race_id
ORDER BY vp.lot_id, vp.race_id, v.date, vp.id;

CREATE OR REPLACE FUNCTION fn_maj_nombre_poule_vente()
RETURNS TRIGGER AS $$
DECLARE
    v_code_produit   VARCHAR(30);
    v_nombre_actuel  INT;
BEGIN
    SELECT code INTO v_code_produit
    FROM ProduitVente
    WHERE id = NEW.id_produit;

    IF v_code_produit IS DISTINCT FROM 'poule' THEN
        RETURN NEW;
    END IF;

    IF NEW.lot_id IS NULL OR NEW.race_id IS NULL THEN
        RAISE EXCEPTION 'lot_id et race_id sont obligatoires pour une vente de poule';
    END IF;

    SELECT nombre INTO v_nombre_actuel
    FROM lot_races
    WHERE lot_id = NEW.lot_id
      AND race_id = NEW.race_id;

    IF v_nombre_actuel IS NULL THEN
        RAISE EXCEPTION 'Aucune ligne lot_races trouvée pour le lot % / race %', NEW.lot_id, NEW.race_id;
    END IF;

    IF v_nombre_actuel - NEW.quantite < 0 THEN
        RAISE EXCEPTION 'Stock de poules insuffisant pour le lot % / race % (restant: %, demandé: %)',
            NEW.lot_id, NEW.race_id, v_nombre_actuel, NEW.quantite;
    END IF;

    UPDATE lot_races
    SET nombre = nombre - NEW.quantite
    WHERE lot_id = NEW.lot_id
      AND race_id = NEW.race_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_maj_nombre_poule_vente ON DetailVente;

CREATE TRIGGER trg_maj_nombre_poule_vente
AFTER INSERT ON DetailVente
FOR EACH ROW
EXECUTE FUNCTION fn_maj_nombre_poule_vente();
