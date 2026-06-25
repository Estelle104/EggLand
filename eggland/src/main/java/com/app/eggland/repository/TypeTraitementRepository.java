package com.app.eggland.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.TypeTraitement;

@Repository
public interface TypeTraitementRepository extends JpaRepository<TypeTraitement, Integer> {
    Optional<TypeTraitement> findByCode(String code);
}
