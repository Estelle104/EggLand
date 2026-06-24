package com.app.eggland.repository;

import com.app.eggland.model.Employe;
import com.app.eggland.model.PaiementSalaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaiementSalaireRepository extends JpaRepository<PaiementSalaire, Integer> {

    List<PaiementSalaire> findByMoisOrderByEmployeNomAsc(LocalDate mois);

    List<PaiementSalaire> findAllByOrderByMoisDescDatePaiementDesc();

    Optional<PaiementSalaire> findByEmployeAndMois(Employe employe, LocalDate mois);
}
