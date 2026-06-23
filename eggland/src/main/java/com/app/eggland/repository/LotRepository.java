package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Batiment;
import com.app.eggland.model.Lot;
import java.util.List;
@Repository
public interface LotRepository extends JpaRepository<Lot, Integer>{
       
   @Query("""
    SELECT COALESCE(SUM(l.nombreInitial), 0)
    FROM Lot l
    WHERE l.batiment = :batiment
""")
    int calculerPlaceUtiliseePourBatiment(@Param("batiment") Batiment batiment);
    
        List<Lot> findByBatimentAndStatut(Batiment batiment, String statut);   
}