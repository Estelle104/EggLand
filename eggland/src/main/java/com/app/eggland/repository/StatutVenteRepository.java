package com.app.eggland.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.app.eggland.model.StatutVente;

@Repository
public interface StatutVenteRepository extends JpaRepository<StatutVente, Integer> {
    Optional<StatutVente> findByCode(String code);
    
}
