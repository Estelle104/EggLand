package com.app.eggland.service;

import org.springframework.stereotype.Service;

import com.app.eggland.model.Race;
import com.app.eggland.repository.RaceRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class RaceService {
    @Autowired
    private RaceRepository raceRepository;

    public List<Race> findAll(){
        return raceRepository.findAll();
    }
    public Optional<Race> findById(Integer id) { 
        return raceRepository.findById(id); 
    }
    public Race save(Race race) { 
        return raceRepository.save(race); 
    }
    public void deleteById(Integer id) { 
        raceRepository.deleteById(id); 
    }
    
}
