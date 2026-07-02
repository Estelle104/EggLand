package com.app.eggland.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Service
public class RechercheService {

    @Autowired
    private EntityManager entityManager;

    // Recherche dans toutes les colonnes de type texte et numérique des tables de la base de données
    public List<RechercheResultat> rechercher(String motCle) {
        List<RechercheResultat> results = new ArrayList<>();
        if (motCle == null || motCle.isBlank()) return results;

        String likePattern = "%" + motCle.toLowerCase() + "%";

        List<TableColumn> colonnes = getColonnesTexte();

        for (TableColumn tc : colonnes) {
            try {
                String sql = "SELECT id, " + tc.column + " AS valeur FROM " + tc.table
                           + " WHERE LOWER(CAST(" + tc.column + " AS TEXT)) LIKE :mot";
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("mot", likePattern);

                @SuppressWarnings("unchecked")
                List<Object[]> rows = query.getResultList();
                for (Object[] row : rows) {
                    Number id = (Number) row[0];
                    String valeur = row[1] != null ? row[1].toString() : "";
                    results.add(new RechercheResultat(tc.table, tc.column, id.intValue(), valeur));
                }
            } catch (Exception e) {
                // ignorer les tables qui posent probleme (ex: pas de colonne id)
            }
        }

        return results;
    }

    @SuppressWarnings("unchecked")
    private List<TableColumn> getColonnesTexte() {
        String sql = """
            SELECT table_name, column_name
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND data_type IN (
                'character varying', 'character', 'text',
                'integer', 'bigint', 'smallint',
                'numeric', 'decimal', 'real', 'double precision', 'float', 'money', 'smallmoney'
              )
              AND table_name NOT ILIKE '%statut%'
              AND table_name NOT ILIKE '%type%'
              AND table_name NOT ILIKE '%role%'
              AND table_name NOT IN ('flyway_schema_history', 'databasechangelog', 'databasechangeloglock')
            ORDER BY table_name, ordinal_position
        """;
        List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
        List<TableColumn> cols = new ArrayList<>();
        for (Object[] row : rows) {
            cols.add(new TableColumn(row[0].toString(), row[1].toString()));
        }
        return cols;
    }

    public record TableColumn(String table, String column) {}

    public record RechercheResultat(String table, String column, int id, String valeur) {}
}
