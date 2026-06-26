package com.app.eggland.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.repository.OeufStatutRepository;
import com.app.eggland.repository.StatutOeufRepository;
import com.app.eggland.model.OeufStatut;
import com.app.eggland.model.StatutOeuf;

import java.util.ArrayList;
import java.util.List;

@Service
public class OeufStatutService {

    @Autowired
    private OeufStatutRepository oeufStatutRepository;

    @Autowired
    private StatutOeufRepository statutOeufRepository;

    public List<OeufStatut> getAllOeufStatut() {
        return oeufStatutRepository.findAllByOrderByProductionDateDescIdDesc();
    }

    public List<StatutOeuf> getStatutsSaisissables() {
        List<StatutOeuf> resultat = new ArrayList<>();

        for (StatutOeuf statut : statutOeufRepository.findAllByOrderByCodeAsc()) {
            if (statut.getCode() != null
                    && !"valide".equalsIgnoreCase(statut.getCode())
                    && !"vendu".equalsIgnoreCase(statut.getCode())) {
                resultat.add(statut);
            }
        }

        return resultat;
    }
}
