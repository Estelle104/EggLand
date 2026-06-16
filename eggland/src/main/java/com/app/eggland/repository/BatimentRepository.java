package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Batiment;

@Repository
public interface BatimentRepository extends JpaRepository<Batiment, Integer>{    
}