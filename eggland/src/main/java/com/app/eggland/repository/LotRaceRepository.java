package com.app.eggland.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.eggland.model.LotRace;

public interface LotRaceRepository extends JpaRepository<LotRace,Long>{
    List<LotRace> findByLotId(Integer lotId);
    LotRace findByLotIdAndRaceId(Integer lotId, Integer raceId);
}
