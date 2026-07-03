package com.app.eggland.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.app.eggland.model.Batiment;
import com.app.eggland.repository.BatimentRepository;

@Service
public class BatimentService {
    @Autowired
    private BatimentRepository batimentRepository;

    public List<Batiment> findAll() {
        return batimentRepository.findAll();
    }

    public Batiment save(Batiment batiment) {
        return batimentRepository.save(batiment);
    }

    public void deleteById(Integer id) {
        batimentRepository.deleteById(id);
    }

    public Optional<Batiment> findById(Integer id) {
        return batimentRepository.findById(id);
    }
    
    /*méthode pour la pagination*/
    public List<Batiment> getPage(List<Batiment> batiments, int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, batiments.size());
        if (start > end) {
            return List.of(); // Retourne une liste vide si la page demandée est hors limites
        }
        return batiments.subList(start, end);
    }
}
