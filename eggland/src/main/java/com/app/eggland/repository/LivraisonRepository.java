package com.app.eggland.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Livraison;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Integer>{

    List<Livraison> findAllByOrderByDateLivraisonDesc();

    List<Livraison> findByDateLivraisonBetweenOrderByDateLivraisonDesc(LocalDate debut, LocalDate fin);

    List<Livraison> findByDateLivraisonAfterOrderByDateLivraisonDesc(LocalDate date);

    List<Livraison> findByDateLivraisonBeforeOrderByDateLivraisonDesc(LocalDate date);

    List<Livraison> findByDateLivraison(LocalDate date);

    @Query("SELECT COUNT(l) FROM Livraison l WHERE l.statut.code = :code")
    long countByStatutCode(@Param("code") String code);

    List<Livraison> findByClientIdOrderByDateLivraisonDesc(Integer clientId);
    List<Livraison> findByClientEmailAndStatutCode(String email,String code);

}
