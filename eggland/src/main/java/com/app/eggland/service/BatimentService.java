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

    public Page<Batiment> findAll(Pageable pageable) {
        return batimentRepository.findAll(pageable);
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
    
}
