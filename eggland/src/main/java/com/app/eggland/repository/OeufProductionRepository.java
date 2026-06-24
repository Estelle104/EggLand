package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.OeufProduction;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

@Repository
public interface OeufProductionRepository extends JpaRepository<OeufProduction, Integer>{  
    boolean existsByLotIdAndDate(Integer lotId, LocalDate date);

    boolean existsByLotIdAndDateAndIdNot(Integer lotId, LocalDate date, Integer productionId);

    List<OeufProduction> findAllByOrderByDateDescIdDesc();

    @Query("SELECT COALESCE(SUM(o.quantite), 0) FROM OeufProduction o")
    Integer sumQuantiteTotale();
}
