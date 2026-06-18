package com.app.eggland.repository;

import java.lang.foreign.Linker.Option;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Client;
import java.util.List;



@Repository
public interface ClientRepository extends JpaRepository<Client, Integer>{    
    Optional<Client> findByEmail(String email);
    //pour eviter les doublons 
    boolean existsByEmail(String email);
}