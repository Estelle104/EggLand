package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Client;


@Repository
public interface ClientRepository extends JpaRepository<Client, Integer>{    
}