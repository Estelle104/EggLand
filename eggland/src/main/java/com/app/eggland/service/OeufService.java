package com.app.eggland.service;

import com.app.eggland.repository.StatutOeufRepository;
import com.app.eggland.repository.OeufStatutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class OeufService {

    @Autowired
    private StatutOeufRepository statutOeufRepository;

    @Autowired
    private OeufStatutRepository oeufStatutRepository;

    public Integer getStockDisponible() {
        return statutOeufRepository.findByCode("valide")
                .map(statut -> oeufStatutRepository.sumQuantiteByStatutId(statut.getId()))
                .orElse(0);
    }
}