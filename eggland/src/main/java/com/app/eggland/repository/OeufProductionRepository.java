package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.OeufProduction;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OeufProductionRepository extends JpaRepository<OeufProduction, Integer>{  

     void deleteByLotId(Integer lotId);


    boolean existsByLotIdAndDate(Integer lotId, LocalDate date);

    boolean existsByLotIdAndDateAndIdNot(Integer lotId, LocalDate date, Integer productionId);

    List<OeufProduction> findAllByOrderByDateDescIdDesc();

    @Query("SELECT COALESCE(SUM(o.quantite), 0) FROM OeufProduction o")
    Integer sumQuantiteTotale();

    @Query("""
            SELECT o.date, SUM(o.quantite)
            FROM OeufProduction o
            WHERE o.date BETWEEN :dateDebut AND :dateFin
            GROUP BY o.date
            ORDER BY o.date
            """)
    List<Object[]> sumQuantiteParDate(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    @Query(value = "SELECT * FROM v_historique_production", nativeQuery = true)
    List<Map<String, Object>> findHistoriqueProduction();

    List<OeufProduction> findByDateBetweenOrderByDateDesc(LocalDate debut, LocalDate fin);
}

