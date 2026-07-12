package com.app.eggland.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.eggland.model.Lot;
import com.app.eggland.model.Traitement;
import com.app.eggland.repository.TraitementRepository;

@Service
public class TraitementService {

    @Autowired
    private TraitementRepository traitementRepository;

    @Autowired
    private MvtArgentService mvtArgentService;

    @Transactional
    public Traitement save(Traitement traitement) {
        if (traitement.getDate() == null) {
            traitement.setDate(LocalDate.now());
        }
        if (traitement.getCout() == null) {
            traitement.setCout(BigDecimal.ZERO);
        }

        Traitement savedTraitement = traitementRepository.save(traitement);

        // Créer un mouvement de sortie d'argent si le coût est positif
        if (traitement.getCout().compareTo(BigDecimal.ZERO) > 0) {
            // Référence unique basée sur l'ID du traitement pour éviter les doublons
            String reference = "TRAITEMENT-" + savedTraitement.getId() + "-" + System.currentTimeMillis();
            mvtArgentService.creerSortieAvecReference(
                traitement.getCout(),
                traitement.getDate(),
                "traitement_veterinaire",
                reference
            );
        }

        return savedTraitement;
    }

    public List<Traitement> findAll() {
        return traitementRepository.findAll();
    }

    public Optional<Traitement> findById(Integer id) {
        return traitementRepository.findById(id);
    }

    public void deleteById(Integer id) {
        traitementRepository.deleteById(id);
    }

    public List<Traitement> findByLot(Lot lot) {
        return traitementRepository.findByLot(lot);
    }
}
