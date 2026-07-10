package com.app.eggland.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Client;

import jakarta.persistence.criteria.CriteriaBuilder.In;



@Repository
public interface ClientRepository extends JpaRepository<Client, Integer>{    
    Optional<Client> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Client> findByNom(String nom);
    Optional<Client> findByNomContainingIgnoreCase(String nom);
    Optional<Client> findById(Integer id);
}