package com.app.eggland.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.Lot;
import com.app.eggland.model.Mort;
import com.app.eggland.repository.LotRepository;
import com.app.eggland.repository.MortRepository;

@Service
public class MortService {
    @Autowired
    private MortRepository mortRepository;

    @Autowired
    private LotRepository lotRepository;

    /// CRUD
    public Mort save(Mort mort) {
        if (mort.getDate() == null) {
            mort.setDate(LocalDate.now());
        }
        return mortRepository.save(mort);
    }

    public List<Mort> findAll() {
        return mortRepository.findAll();
    }

    public Optional<Mort> findById(Integer id) {
        return mortRepository.findById(id);
    }

    public void deleteById(Integer id) {
        mortRepository.deleteById(id);
    }
    //fonction lot temporaire  (git conflict)
    public List<Lot> findAllLotTemporary() {
        return lotRepository.findAll();
    }

    public List<Mort> findByLot(Lot lot) {
        return mortRepository.findByLot(lot);
    }

    // nombre de poule vivant dans un lot
    public Integer getNombreActuel(Lot lot) {
        Integer totalMorts = mortRepository.sumMortalityByLot(lot);
        return lot.getNombreInitial() - totalMorts;
    }

    // nombre total mort
    public Integer getTotalMort() {
        List<Lot> lots = lotRepository.findAll();
        Integer total = 0;
        for(Lot lot : lots)
            total += mortRepository.sumMortalityByLot(lot);

        return total;
    }

    //mort par lot
    public Integer getNombreMort(Lot lot) {
        return mortRepository.sumMortalityByLot(lot);
    }
}

