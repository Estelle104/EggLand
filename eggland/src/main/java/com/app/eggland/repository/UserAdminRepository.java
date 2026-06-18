package com.app.eggland.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.UserAdmin;
import java.util.List;


@Repository
public interface UserAdminRepository extends JpaRepository<UserAdmin, Integer>{    
    Optional<UserAdmin> findByEmail(String email);
    boolean existsByEmail(String email);
}