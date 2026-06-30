package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Nourriture;

@Repository
public interface NourritureRepository extends JpaRepository<Nourriture, Integer>{    
}