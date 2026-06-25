package com.app.eggland.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Lot;
import com.app.eggland.model.Traitement;

@Repository
public interface TraitementRepository extends JpaRepository<Traitement, Integer> {
    List<Traitement> findByLot(Lot lot);
    
    List<Traitement> findByLotAndDateBetween(Lot lot, LocalDate startDate, LocalDate endDate);
}
