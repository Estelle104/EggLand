package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.UserAdmin;

@Repository
public interface UserAdminRepository extends JpaRepository<UserAdmin, Integer>{    
}