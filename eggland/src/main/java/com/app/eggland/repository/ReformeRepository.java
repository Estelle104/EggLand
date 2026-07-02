package com.app.eggland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Reforme;

@Repository
public interface ReformeRepository extends JpaRepository<Reforme, Integer>{   
      void deleteByLotId(Integer lotId);

      @Query("SELECT SUM(r.nombre) FROM Reforme r WHERE r.lot.id = :lotId")
      Long sumByLotId(@Param("lotId") Integer lotId);
}