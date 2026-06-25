package com.app.eggland.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.Lot;
import com.app.eggland.repository.LotRepository;

@Service
public class LotService {
    @Autowired
    private LotRepository lotRepository;

    public List<Lot> findAll() {
        return lotRepository.findAll();
    }
    
    public Optional<Lot> findById(Integer id) {
        return lotRepository.findById(id);
    }
    
    public Lot save(Lot lot) {
        return lotRepository.save(lot);
    }
    
    public void deleteById(Integer id) {
        lotRepository.deleteById(id);
    }
}
