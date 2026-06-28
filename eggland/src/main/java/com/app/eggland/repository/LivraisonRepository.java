package com.app.eggland.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Livraison;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Integer>{

    List<Livraison> findAllByOrderByDateLivraisonDesc();

    List<Livraison> findByDateLivraisonBetweenOrderByDateLivraisonDesc(LocalDate debut, LocalDate fin);

    List<Livraison> findByDateLivraisonAfterOrderByDateLivraisonDesc(LocalDate date);

    List<Livraison> findByDateLivraisonBeforeOrderByDateLivraisonDesc(LocalDate date);
}
