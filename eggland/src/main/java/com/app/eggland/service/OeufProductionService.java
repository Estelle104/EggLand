package com.app.eggland.service;

import com.app.eggland.model.OeufProduction;
import com.app.eggland.model.OeufStatut;
import com.app.eggland.model.StatutOeuf;
import com.app.eggland.repository.OeufProductionRepository;
import com.app.eggland.repository.StatutOeufRepository; // Ne pas oublier
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class OeufProductionService {
    
    @Autowired
    private OeufProductionRepository oeufProductionRepository;

    @Autowired
    private StatutOeufRepository statutOeufRepository;

    @Transactional
    public OeufProduction enregistrerOeufProduction(OeufProduction oeufProduction) {
        if (oeufProduction.getDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Date future interdite");
        }

        if (oeufProduction.getLot() != null && oeufProductionRepository.existsByLotIdAndDate(oeufProduction.getLot().getId(), oeufProduction.getDate())) {
            throw new RuntimeException("Production déjà saisie pour ce lot à cette date");
        }

        int quantiteTotale = oeufProduction.getQuantite();

        // si aucun statut fourni 
        if (oeufProduction.getOeufStatuts() == null || oeufProduction.getOeufStatuts().isEmpty()) {
            oeufProduction.setOeufStatuts(new ArrayList<>());
            
            StatutOeuf statutValider = statutOeufRepository.findByNom("valider")
                .orElseThrow(() -> new RuntimeException("Statut 'valider' introuvable en BDD"));

            OeufStatut uniqueStatut = OeufStatut.builder()
                    .production(oeufProduction)
                    .statut(statutValider)
                    .quantite(quantiteTotale)
                    .build();
            
            oeufProduction.getOeufStatuts().add(uniqueStatut);
        } 
        else {
            int sommeQuantitesSaisies = 0;
            OeufStatut statutValiderExistant = null;

            for (OeufStatut os : oeufProduction.getOeufStatuts()) {
                os.setProduction(oeufProduction); 
                sommeQuantitesSaisies += os.getQuantite();
                
                if (os.getStatut() != null && "valider".equals(os.getStatut().getNom())) {
                    statutValiderExistant = os;
                }
            }

            if (sommeQuantitesSaisies < quantiteTotale) {
                int resteAValider = quantiteTotale - sommeQuantitesSaisies;

                if (statutValiderExistant != null) {
                    statutValiderExistant.setQuantite(statutValiderExistant.getQuantite() + resteAValider);
                } else {
                    StatutOeuf statutValider = statutOeufRepository.findByNom("valider")
                        .orElseThrow(() -> new RuntimeException("Statut 'valider' introuvable"));
                    
                    OeufStatut resteStatut = OeufStatut.builder()
                            .production(oeufProduction)
                            .statut(statutValider)
                            .quantite(resteAValider)
                            .build();
                    
                    oeufProduction.getOeufStatuts().add(resteStatut);
                }
            }
            
            if (sommeQuantitesSaisies > quantiteTotale) {
                throw new IllegalArgumentException("La somme des statuts ne peut pas dépasser la quantité totale produite.");
            }
        }

        return oeufProductionRepository.save(oeufProduction);
    }
}