package com.app.eggland.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.Nourriture;
import com.app.eggland.repository.NourritureRepository;

@Service
public class NourritureService {
    @Autowired
    private NourritureRepository nourritureRepository;

    public List<Nourriture> findAll() {
        return nourritureRepository.findAll();
    }

    //recherche par libelle de nourriture(par keyword)
    public List<Nourriture> findByLibelleContaining(String keyword) {
        return nourritureRepository.findByLibelleContainingIgnoreCase(keyword);
    }

    public Optional<Nourriture> findById(Integer id) {
        return nourritureRepository.findById(id);
    }

    public Nourriture save(Nourriture nourriture) {
        return nourritureRepository.save(nourriture);
    }

    public void deleteById(Integer id) {
        nourritureRepository.deleteById(id);
    }
}
