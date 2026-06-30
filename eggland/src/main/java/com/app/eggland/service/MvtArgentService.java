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

    public MvtArgent creerSortie(BigDecimal montant, LocalDate date, String categorie) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (montant == null) {
            montant = BigDecimal.ZERO;
        }

        TypeMvt sortie = typeMvtRepository.findByCode("sortie")
                .orElseThrow(() -> new RuntimeException("Type 'sortie' introuvable"));

        MvtArgent mvt = MvtArgent.builder()
                .type(sortie)
                .montant(montant)
                .date(date)
                .categorie(categorie)
                .build();
        return mvtArgentRepository.save(mvt);
    }
}
