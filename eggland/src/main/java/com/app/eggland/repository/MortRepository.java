package com.app.eggland.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.Lot;
import com.app.eggland.model.Mort;

@Repository
public interface MortRepository extends JpaRepository<Mort, Integer> {

  @Query("SELECT SUM(m.nombre) FROM Mort m WHERE m.lot.id = :lotId AND m.date BETWEEN :debut AND :fin")
  Long sumByLotIdAndDateBetween(@Param("lotId") Integer lotId, @Param("debut") LocalDate debut,
      @Param("fin") LocalDate fin);

  @Query("SELECT SUM(m.nombre) FROM Mort m WHERE m.date BETWEEN :debut AND :fin")
  Long sumByDateBetween(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

  @Query("SELECT COALESCE(SUM(m.nombre), 0) FROM Mort m WHERE m.lot.id = :lotId")
  Long sumByLotId(@Param("lotId") Integer lotId);

  List<Mort> findByLot(Lot lot);

  List<Mort> findByLotAndDateBetween(Lot lot, LocalDate startDate, LocalDate endDate);

  List<Mort> findAllByOrderByDateDesc();

  List<Mort> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

  @Query("SELECT COALESCE(SUM(m.nombre), 0) FROM Mort m WHERE m.lot = :lot")
  Integer sumMortalityByLot(@Param("lot") Lot lot);

  void deleteByLotId(Integer lotId);

  @Query("""
      SELECT COALESCE(SUM(m.nombre),0)
      FROM Mort m
      WHERE m.lot.id = :lotId
      AND m.race.id = :raceId
      """)
  Long sumByLotAndRace(Integer lotId, Integer raceId);
  
}