package com.app.eggland.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Nourriture;

//recherche par libelle de nourriture
@Repository
public interface NourritureRepository extends JpaRepository<Nourriture, Integer>{
    List<Nourriture> findByLibelleContainingIgnoreCase(String libelle);
}