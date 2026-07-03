package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.app.eggland.model.Employe;

import java.util.List;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Integer>{    
    Page<Employe> findAllByOrderByNomAscPrenomAsc(Pageable pageable);
}