package com.app.eggland.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.MvtArgent;
import com.app.eggland.model.TypeMvt;
import com.app.eggland.repository.MvtArgentRepository;
import com.app.eggland.repository.TypeMvtRepository;

@Service
public class MvtArgentService {
    @Autowired
    private MvtArgentRepository mvtArgentRepository;

    @Autowired
    private TypeMvtRepository typeMvtRepository;

    //creer un mouvement d'argent de type "sortie" pour un achat de nourriture
    public MvtArgent creerSortie(BigDecimal montant, LocalDate date, String categorie) {

        TypeMvt sortie = typeMvtRepository.findByCodeIgnoreCase("sortie")
                .orElseThrow(() -> new RuntimeException("Type 'sortie' introuvable"));

        MvtArgent mvt = MvtArgent.builder()
                .type(sortie)
                .montant(montant)
                .date(date)
                .categorie(categorie)
                .build();
        return mvtArgentRepository.save(mvt);   
    }

    public MvtArgent creerEntree(BigDecimal montant, LocalDate date, String categorie) {

        TypeMvt entree = typeMvtRepository.findByCodeIgnoreCase("entree")
                .orElseThrow(() -> new RuntimeException("Type 'entree' introuvable"));

        MvtArgent mvt = MvtArgent.builder()
                .type(entree)
                .montant(montant)
                .date(date)
                .categorie(categorie)
                .build();
        return mvtArgentRepository.save(mvt);   
    }
}
