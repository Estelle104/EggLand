package com.app.eggland.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.DetailVente;

@Repository
public interface DetailVenteRepository extends JpaRepository<DetailVente, Integer>{  
    List<DetailVente> findByVenteId(Integer id);

}