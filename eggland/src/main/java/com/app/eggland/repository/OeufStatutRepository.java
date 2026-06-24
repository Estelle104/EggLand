package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.eggland.model.OeufStatut;

@Repository
public interface OeufStatutRepository extends JpaRepository<OeufStatut, Integer>{  
    @Query("SELECT COALESCE(SUM(o.quantite), 0) FROM OeufStatut o WHERE o.statut.id = :statutId")
    Integer sumQuantiteByStatutId(@Param("statutId") Integer statutId);
}