
package com.app.eggland.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.eggland.model.*;
import com.app.eggland.repository.*;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
@Service
public class LotService {
    @Autowired
    LotRepository lotRepository;
    @Autowired
    BatimentRepository batimentRepository;

    @Autowired
    StatutLotRepository statutLotRepository;


    @Autowired
    ReformeRepository reformeRepository;

    @Autowired
    TraitementRepository traitementRepository;

    @Autowired
    OeufProductionRepository oeufProductionRepository;

    @Autowired
    MortRepository mortRepository;

    @Autowired
    LotRaceRepository lotRaceRepository;

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
lotRaceRepository.saveAll(lot.getLotRaces());

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

    //rehefa mapiditra lot vaovao
   public int calculerPlaceUtilisee(Batiment batiment) {
        return lotRepository.calculerPlaceUtiliseePourBatiment(batiment);
    }

    public int getPlaceRestante(Integer idBatiment){
        int placeRestante;
            Batiment batiment = batimentRepository.findById(idBatiment)
            .orElseThrow(() -> new IllegalArgumentException("Bâtiment non trouvé"));
           
            int capacite = batiment.getCapacite();
            int placeUtilise = lotRepository.calculerPlaceUtiliseePourBatiment(batiment);

            placeRestante = capacite - placeUtilise;
        return placeRestante;
    }
//rehefa reforme

// public void resetCapaciteBatiment(Batiment batiment, StatutLot statut) {
    
   
//     Lot lot = lotRepository.findFirstByStatut(statut);
    
   
//     if (lot == null) {
//         System.out.println("Aucun lot trouvé avec le statut: " + statut);
//         return;
//     }
    
//     int nbrInitiale = lot.getNombreInitial();
//     int placeRestante = getPlaceRestante(batiment.getId());
//     int placeBatiment = 0;
   
//     if ("REFORME".equals(lot.getStatut().getCode())) {
       
//         placeBatiment = nbrInitiale + placeRestante;
//     } else {
        
//         placeBatiment = placeRestante;
//     }
    
//     System.out.println("Capacite batiment: " + placeBatiment);
//     batiment.setCapacite(placeBatiment);
// }
public int calculerAgeActuel(Lot lot, LocalDate actuel) {

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
public  List<Lot> getAllLots(){
    return lotRepository.findAll();
 }

public  void updateLot(Lot lot){
      Batiment batiment = batimentRepository.findById(lot.getBatiment().getId())
            .orElseThrow(() -> new IllegalArgumentException("Bâtiment non trouvé"));

verifierCapacite(lot,batiment);

    lotRepository.save(lot);
 }

@Transactional
public void deleteLot(Integer id) {

    traitementRepository.deleteByLotId(id);
    oeufProductionRepository.deleteByLotId(id);
    mortRepository.deleteByLotId(id);
    reformeRepository.deleteByLotId(id);

    lotRepository.deleteById(id);
}

public  Lot findById(Integer idLot){
    return lotRepository.findById(idLot).orElse(null);
 }

 public List<Lot> findByBatimentOrStatut(Batiment batiment,StatutLot statutLot){

    return lotRepository.findByBatimentOrStatut(batiment, statutLot);
 }

 public List<Lot> findByBatimentAndStatut(Batiment batiment,StatutLot statutLot){
    
    return lotRepository.findByBatimentAndStatut(batiment, statutLot);
 }


@Transactional
public void reformerUnLot(Integer idLot, LocalDate dateReforme) {
    
  
    
  
    if (idLot == null) {
        throw new IllegalArgumentException("Id lot introuvable");
    }
    

    Lot lot = findById(idLot);
    System.out.println("Lot trouvé: " + (lot != null ? lot.getId() : "NULL"));
    
    if (lot == null || lot.getId() == null) {
        throw new IllegalArgumentException("Lot invalide avec l'ID: " + idLot);
    }
  
    StatutLot statut = statutLotRepository.findById(2)
        .orElseThrow(() -> new IllegalArgumentException("Statut REFORME introuvable"));
    System.out.println("Statut REFORME trouvé: " + statut.getCode());

    if (dateReforme == null || dateReforme.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("Date de réforme invalide: " + dateReforme);
    }
 
    lot.setStatut(statut);
    System.out.println("Statut lot changé à: " + lot.getStatut().getCode());
    
 if(dateReforme.isBefore(lot.getDateArrivee())){
            throw new IllegalArgumentException("La date:"+dateReforme+"ne peut pas être avant la date d'arrivé"+lot.getDateArrivee());

 }
    updateLot(lot); 
    System.out.println("Lot sauvegardé dans la base");
    
 
    int nbrPoule = lot.getNombreInitial();
    System.out.println("Nombre de poules à réformer: " + nbrPoule);
    


    Reforme reforme = new Reforme();
    reforme.setLot(lot);
    reforme.setNombre(nbrPoule);
    reforme.setDate(dateReforme);
    
    System.out.println("Réforme créée - avant save");
    
    reformeRepository.save(reforme);
    
    System.out.println("Réforme sauvegardée avec l'ID: " + reforme.getId());
    System.out.println("Lot " + lot.getId() + " réformé avec " + nbrPoule + " poules");
   
}

public Map<String, Object> getDetailLot(Integer id) {
    return lotRepository.findLotDetail(id);
}

public List<Lot> getAllLotsActifs() {
        return lotRepository.findAllByStatutCodeIgnoreCaseOrderByIdAsc("actif");
    }
}

