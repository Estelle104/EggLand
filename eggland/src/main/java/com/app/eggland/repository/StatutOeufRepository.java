package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.StatutOeuf;
import java.util.List;
import java.util.Optional;


@Repository
public interface StatutOeuftRepository extends JpaRepository<StatutClient,Integer>{
    Optional<StatutOeuf> findByCode(String code);
}
