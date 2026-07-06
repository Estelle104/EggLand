package com.app.eggland.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Batiment;
import com.app.eggland.model.Lot;
import com.app.eggland.model.StatutLot;

@Repository
public interface LotRepository extends JpaRepository<Lot, Integer>{
       
   @Query("""
    SELECT SUM(l.nombreInitial)
    FROM Lot l
    WHERE l.batiment = :batiment
""")
    Long calculerPlaceUtiliseePourBatiment(@Param("batiment") Batiment batiment);
    
    @Query("SELECT SUM(l.nombreInitial) FROM Lot l WHERE l.batiment = :batiment AND l.id != :lotId")
    Long calculerPlaceUtiliseePourBatimentExcluantLot(@Param("batiment") Batiment batiment, @Param("lotId") Integer lotId);
    
    List<Lot> findByBatimentOrStatut(Batiment batiment, StatutLot statutLot); 
    
    List<Lot> findByBatimentAndStatut(Batiment batiment, StatutLot statutLot);
    
    @Query("SELECT l FROM Lot l WHERE l.statut = :statut ORDER BY l.id ASC LIMIT 1")
    Lot findFirstByStatut(@Param("statut") StatutLot statutLot);

    @Query(value = "SELECT * FROM v_lot_detail WHERE lot_id = :id", nativeQuery = true)
    List<Map<String, Object>> findLotDetail(@Param("id") Integer id);

    List<Lot> findAllByStatutCodeIgnoreCaseOrderByIdAsc(String code);
    
    boolean existsByBatimentId(Integer batimentId);
    
    // NOUVELLES MÉTHODES POUR LA SIMULATION DE MORTALITÉ
    
    /**
     * Récupère les lots actifs avec leur race
     */
    @Query("SELECT DISTINCT l FROM Lot l JOIN FETCH l.race WHERE LOWER(l.statut.code) = 'actif' ORDER BY l.id ASC")
    List<Lot> findActiveLotsWithRace();
    
    /**
     * Récupère un lot avec sa race par son ID
     */
    @Query("SELECT l FROM Lot l JOIN FETCH l.race WHERE l.id = :id")
    Optional<Lot> findByIdWithRace(@Param("id") Integer id);
    
    /**
     * Récupère les lots actifs
     */
    @Query("SELECT l FROM Lot l WHERE LOWER(l.statut.code) = 'actif' ORDER BY l.id ASC")
    List<Lot> findActiveLots();
}
