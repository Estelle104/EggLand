package com.app.eggland.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

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
        return creerSortieAvecReference(montant, date, categorie, null);
    }

    public Optional<MvtArgent> trouverParReference(String reference) {
        return mvtArgentRepository.findByReference(reference);
    }

    public void supprimerParReference(String reference) {
        mvtArgentRepository.deleteByReference(reference);
    }

    public MvtArgent creerSortieAvecReference(BigDecimal montant, LocalDate date, String categorie, String reference) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (montant == null) {
            montant = BigDecimal.ZERO;
        }

        TypeMvt sortie = typeMvtRepository.findByCodeIgnoreCase("sortie")
                .orElseThrow(() -> new RuntimeException(
                        "Type 'sortie' introuvable en base de données. Vérifiez la table typemvt."));

        MvtArgent mvt = MvtArgent.builder()
                .type(sortie)
                .montant(montant)
                .date(date)
                .categorie(categorie)
                .reference(reference)
                .build();
        return mvtArgentRepository.save(mvt);
    }

    public MvtArgent creerEntree(BigDecimal montant, LocalDate date, String categorie) {
        return creerEntreeAvecReference(montant, date, categorie, null);
    }

    public MvtArgent creerEntreeAvecReference(BigDecimal montant, LocalDate date, String categorie, String reference) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (montant == null) {
            montant = BigDecimal.ZERO;
        }

        TypeMvt entree = typeMvtRepository.findByCodeIgnoreCase("entree")
                .orElseThrow(() -> new RuntimeException(
                        "Type 'entree' introuvable en base de données. Vérifiez la table typemvt."));

        MvtArgent mvt = MvtArgent.builder()
                .type(entree)
                .montant(montant)
                .date(date)
                .categorie(categorie)
                .reference(reference)
                .build();
        return mvtArgentRepository.save(mvt);
    }

    public MvtArgent saveMvt(MvtArgent mvt) {
        return mvtArgentRepository.save(mvt);
    }
}
