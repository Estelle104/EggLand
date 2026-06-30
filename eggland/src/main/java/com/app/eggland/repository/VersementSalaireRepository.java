package com.app.eggland.repository;

import com.app.eggland.model.PaiementSalaire;
import com.app.eggland.model.VersementSalaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VersementSalaireRepository extends JpaRepository<VersementSalaire, Integer> {

    List<VersementSalaire> findByPaiementSalaireOrderByDateAsc(PaiementSalaire paiementSalaire);

    List<VersementSalaire> findAllByOrderByDateDesc();
}