package com.app.eggland.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findAllByOrderByDateCreationDesc();

    List<Notification> findByLuFalseOrderByDateCreationDesc();

    long countByLuFalse();

    @Modifying
    @Query("UPDATE Notification n SET n.lu = true WHERE n.id = :id")
    void marquerCommeLu(@Param("id") Integer id);
}
