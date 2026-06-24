package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Mort;

@Repository
public interface MortRepository extends JpaRepository<Mort, Integer>{   
      void deleteByLotId(Integer lotId); 
}