package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.OeufProduction;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface OeufProductionRepository extends JpaRepository<OeufProduction, Integer>{  

     void deleteByLotId(Integer lotId);


    boolean existsByLotIdAndDate(Integer lotId, LocalDate date);

    boolean existsByLotIdAndDateAndIdNot(Integer lotId, LocalDate date, Integer productionId);

    List<OeufProduction> findAllByOrderByDateDescIdDesc();

    @Query("SELECT SUM(o.quantite) FROM OeufProduction o")
    Long sumQuantiteTotale();

    @Query("SELECT SUM(o.quantite) FROM OeufProduction o WHERE o.date = :date")
    Long sumQuantiteByDate(@Param("date") LocalDate date);

    @Query("""
            SELECT o.date, SUM(o.quantite)
            FROM OeufProduction o
            WHERE o.date BETWEEN :dateDebut AND :dateFin
            GROUP BY o.date
            ORDER BY o.date
            """)
    List<Object[]> sumQuantiteParDate(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    @Query("""
            SELECT o.date, SUM(o.quantite)
            FROM OeufProduction o
            WHERE o.date BETWEEN :dateDebut AND :dateFin
            AND (:lotId IS NULL OR o.lot.id = :lotId)
            GROUP BY o.date
            ORDER BY o.date
            """)
    List<Object[]> sumQuantiteParDatePourLot(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin, @Param("lotId") Integer lotId);

    @Query(value = "SELECT * FROM v_historique_production", nativeQuery = true)
    List<Map<String, Object>> findHistoriqueProduction();

    @Query(value = "SELECT * FROM v_historique_production ORDER BY date DESC, lot_id DESC",
        countQuery = "SELECT COUNT(*) FROM v_historique_production",
        nativeQuery = true)
        Page<Map<String, Object>> findHistoriqueProduction(Pageable pageable);

    List<OeufProduction> findByDateBetweenOrderByDateDesc(LocalDate debut, LocalDate fin);
}
