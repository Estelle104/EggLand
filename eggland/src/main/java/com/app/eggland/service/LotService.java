package com.app.eggland.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.eggland.model.*;
import com.app.eggland.repository.*;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Service
public class LotService {
    @Autowired
    LotRepository lotRepository;
    @Autowired
    BatimentRepository batimentRepository;

    @Autowired
    StatutLotRepository statutLotRepository;
   @Transactional
    public void createLot(Lot lot){
         Batiment batiment = batimentRepository.findById(lot.getBatiment().getId())
            .orElseThrow(() -> new IllegalArgumentException("Bâtiment non trouvé"));

verifierCapacite(lot,batiment);
    lot.setAgeSemaine(24);

      StatutLot actif = statutLotRepository.findById(1)
        .orElseThrow(() -> new IllegalArgumentException("Statut ACTIF introuvable"));

    lot.setStatut(actif);
lot.setBatiment(batiment);

lotRepository.save(lot);

    }

    public void verifierCapacite(Lot lot,Batiment batiment){
        int capacite = batiment.getCapacite();
        int nbrInitiale = lot.getNombreInitial();

        if(capacite < nbrInitiale){
             throw new IllegalArgumentException(
                " Le nombre initial (" + nbrInitiale + 
                ") dépasse la capacité du bâtiment (" + capacite + ")"
            );
        }

        int placeUtilise = calculerPlaceUtilisee(batiment);
        int placeRestante = capacite - placeUtilise;

        if(nbrInitiale > placeRestante){
            throw new IllegalArgumentException(
                   " Place insuffisante! " +
                "Capacité: " + capacite + 
                " | Utilisée: " + placeUtilise + 
                " | Restante: " + placeRestante +
                " | Demandé: " + nbrInitiale
            );

        }
    }
   private int calculerPlaceUtilisee(Batiment batiment) {
        return lotRepository.calculerPlaceUtiliseePourBatiment(batiment);
    }

    private int getPlaceRestante(Integer idBatiment){
        int placeRestante;
            Batiment batiment = batimentRepository.findById(idBatiment)
            .orElseThrow(() -> new IllegalArgumentException("Bâtiment non trouvé"));
           
            int capacite = batiment.getCapacite();
            int placeUtilise = lotRepository.calculerPlaceUtiliseePourBatiment(batiment);

            placeRestante = capacite - placeUtilise;
        return placeRestante;
    }

private int calculerAgeActuel(Lot lot, LocalDate actuel) {

    LocalDate dateEntree = lot.getDateArrivee();
if(dateEntree == null){
    throw new IllegalArgumentException("Date inexistant"+dateEntree);
}
    int semainesEcoulees =
            (int) ChronoUnit.WEEKS.between(dateEntree, actuel);
            int ageDepart = lot.getAgeSemaine();

 if (ageDepart == 0) {
        throw new IllegalArgumentException("Âge de départ inexistant");
    }
    

    int ageActuel = ageDepart + semainesEcoulees;
System.out.println("Age de depart : "+ageDepart + "+ semaines ecoule :"+semainesEcoulees+"=ageActuel"+ageActuel);
    return ageActuel;
}
    public int getAgeActuel(Lot lot,LocalDate actuel){
        return calculerAgeActuel(lot,actuel);
    }

}
