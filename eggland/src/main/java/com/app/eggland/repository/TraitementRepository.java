package com.app.eggland.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Traitement;

@Repository
public interface TraitementRepository extends JpaRepository<Traitement, Integer>{    
    void deleteByLotId(Integer lotId);
}