package com.app.eggland.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.ProduitVente;

@Repository
public interface ProduitVenteRepository extends JpaRepository<ProduitVente, Integer> {
    Optional<ProduitVente> findByCode(String code);
}
