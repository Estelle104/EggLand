package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.OeufProduction;

@Repository
public interface OeufProductionRepository extends JpaRepository<OeufProduction, Integer>{    
}