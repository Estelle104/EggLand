package com.app.eggland.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.StatutLivraison;

@Repository
public interface StatutLivraisonRepository extends JpaRepository<StatutLivraison, Integer> {
    Optional<StatutLivraison> findByCode(String code);
}
