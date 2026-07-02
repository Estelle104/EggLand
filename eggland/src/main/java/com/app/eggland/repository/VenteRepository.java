package com.app.eggland.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Vente;

@Repository
public interface VenteRepository extends JpaRepository<Vente, Integer>{ 
    List<Vente> findAllByOrderByDateDesc();

    List<Vente> findByClientIdOrderByDateDesc(@Param("clientId") Integer clientId);

    List<Vente> findByStatutIdOrderByDateDesc(@Param("statutId") Integer statutId);

    List<Vente> findByClientIdAndStatutIdOrderByDateDesc(Integer clientId, Integer statutId);

    List<Vente> findByDateBetweenOrderByDateDesc(LocalDate debut, LocalDate fin);

    List<Vente> findByClientIdAndDateBetweenOrderByDateDesc(Integer clientId, LocalDate debut, LocalDate fin);

    List<Vente> findByStatutIdAndDateBetweenOrderByDateDesc(Integer statutId, LocalDate debut, LocalDate fin);

    List<Vente> findByClientIdAndStatutIdAndDateBetweenOrderByDateDesc(Integer clientId, Integer statutId, LocalDate debut, LocalDate fin);

    @Query("SELECT SUM(v.total) FROM Vente v WHERE v.date = :date")
    BigDecimal sumTotalByDate(@Param("date") LocalDate date);
    
    @Query("SELECT DISTINCT v FROM Vente v WHERE v.statut.code IN ('en_attente','paye') AND NOT EXISTS (SELECT 1 FROM Livraison l WHERE l.vente = v AND l.statut.code = 'livre') ORDER BY v.date DESC")
    List<Vente> findVentesNonLivrees();
}