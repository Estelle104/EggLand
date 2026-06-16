package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Lot;

@Repository
public interface LotRepository extends JpaRepository<Lot, Integer>{    
}