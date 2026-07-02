package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.eggland.model.OeufStatut;

import java.util.List;
import java.util.Optional;

@Repository
public interface OeufStatutRepository extends JpaRepository<OeufStatut, Integer>{  
  @Query("SELECT COALESCE(SUM(o.quantite), 0) FROM OeufStatut o WHERE o.statut.id = :statutId")
    Integer sumQuantiteByStatutId(@Param("statutId") Integer statutId); // pour les 

    @Query("SELECT COALESCE(SUM(o.quantite), 0) FROM OeufStatut o WHERE LOWER(o.statut.code) IN ('vendu', 'casse', 'consomme')")
    Integer sumQuantiteIndisponible();

    List<OeufStatut> findAllByOrderByProductionDateDescIdDesc();

    Optional<OeufStatut> findByProductionIdAndStatutCode(Integer productionId, String code);

    Optional<OeufStatut> findFirstByStatutCodeAndQuantiteGreaterThan(String code, Integer quantite);

    List<OeufStatut> findByStatutCodeOrderByProductionDateAsc(String string);

    
}
