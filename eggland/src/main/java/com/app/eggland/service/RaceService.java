package com.app.eggland.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.Batiment;
import com.app.eggland.model.Race;
import com.app.eggland.repository.RaceRepository;

@Service
public class RaceService {
    @Autowired
    private RaceRepository raceRepository;

    public List<Race> findAll() {
        return raceRepository.findAll();
    }

    //pour eviter de retourner null, on utilise Optional
    public Optional<Race> findById(Integer id) {
        return raceRepository.findById(id);
    }

    public Race save(Race race) {
        return raceRepository.save(race);
    }

    public void deleteById(Integer id) {
        raceRepository.deleteById(id);
    }

    /*méthode pour la pagination*/
    public List<Race> getPage(List<Race> races, int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, races.size());
        if (start > end) {
            return List.of(); // Retourne une liste vide si la page demandée est hors limites
        }
        return races.subList(start, end);
    }
}
