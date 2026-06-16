package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Vente;

@Repository
public interface VenteRepository extends JpaRepository<Vente, Integer>{    
}