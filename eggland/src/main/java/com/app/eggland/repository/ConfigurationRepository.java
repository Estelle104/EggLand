package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Configuration;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Integer>{    
}