package com.app.eggland.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Mort;

@Repository
public interface MortRepository extends JpaRepository<Mort, Integer>{   
      void deleteByLotId(Integer lotId);

      @Query("SELECT SUM(m.nombre) FROM Mort m WHERE m.lot.id = :lotId AND m.date BETWEEN :debut AND :fin")
      Long sumByLotIdAndDateBetween(@Param("lotId") Integer lotId, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

      @Query("SELECT SUM(m.nombre) FROM Mort m WHERE m.date BETWEEN :debut AND :fin")
      Long sumByDateBetween(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

      @Query("SELECT COALESCE(SUM(m.nombre), 0) FROM Mort m WHERE m.lot.id = :lotId")
      Long sumByLotId(@Param("lotId") Integer lotId);
}