package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.StatutClient;
import java.util.List;
import java.util.Optional;


@Repository
public interface StatutClientRepository extends JpaRepository<StatutClient,Integer>{
    Optional<StatutClient> findByCode(String code);
}
