package com.app.eggland.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.repository.StatutOeufRepository;
import com.app.eggland.model.StatutOeuf;

import java.util.List;

@Service
public class StatutOeufService {

    @Autowired
    private StatutOeufRepository statutOeufRepository;

    public List<StatutOeuf> getAllStatuts() {
        return statutOeufRepository.findAll();
    }
}
