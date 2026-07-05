package com.app.eggland.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.TypeMvt;

@Repository
public interface TypeMvtRepository extends JpaRepository<TypeMvt, Integer> {
    Optional<TypeMvt> findByCodeIgnoreCase(String code);
}
