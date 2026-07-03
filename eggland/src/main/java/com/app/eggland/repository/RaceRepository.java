package com.app.eggland.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Race;

@Repository
public interface RaceRepository extends JpaRepository<Race, Integer> {
    
    @Query("SELECT r FROM Race r WHERE r.id = :id")
    Optional<Race> findById(@Param("id") Integer id);

    @Query("SELECT r FROM Race r WHERE r.nom = :nom")
    Optional<Race> findByNom(@Param("nom") String nom);
}