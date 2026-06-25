package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Batiment;
import com.app.eggland.model.Lot;
import com.app.eggland.model.StatutLot;

import java.util.List;

import java.util.Map;
@Repository
public interface LotRepository extends JpaRepository<Lot, Integer>{
       
   @Query("""
    SELECT COALESCE(SUM(l.nombreInitial), 0)
    FROM Lot l
    WHERE l.batiment = :batiment
""")
    int calculerPlaceUtiliseePourBatiment(@Param("batiment") Batiment batiment);
    
        List<Lot> findByBatimentOrStatut(Batiment batiment, StatutLot statutLot); 
        List<Lot> findByBatimentAndStatut(Batiment batiment, StatutLot statutLot);
        
         @Query("SELECT l FROM Lot l WHERE l.statut = :statut ORDER BY l.id ASC LIMIT 1")
    Lot findFirstByStatut(@Param("statut") StatutLot statutLot);

      @Query(value = "SELECT * FROM v_lot_detail WHERE lot_id = :id", nativeQuery = true)
Map<String, Object> findLotDetail(@Param("id") Integer id);



List<Lot> findAllByStatutCodeIgnoreCaseOrderByIdAsc(String code);

}

