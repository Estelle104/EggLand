package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.app.eggland.model.StatutVente;

@Repository
public interface StatutVenteRepository extends JpaRepository<StatutVente, Integer> {
    
}
