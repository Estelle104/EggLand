package com.app.eggland.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.dto.SimulationMortaliteResult;
import com.app.eggland.model.Lot;
import com.app.eggland.model.Race;
import com.app.eggland.repository.LotRepository;
import com.app.eggland.repository.MvtArgentRepository;
import com.app.eggland.repository.RaceRepository;

@Service
public class SimulationService {
    @Autowired
    private OeufService oeufService;
    @Autowired
    private MvtArgentRepository mvtArgentRepository;
    @Autowired
    private FinanceService financeService;
    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private RaceRepository raceRepository;

    // Méthode existante
    public int runSimulation(Date dateFin, int nombreOeufs, int prixUnitaire) {
        int nombreDeJours = calculDate(dateFin);
        int nbOeufActuelle = oeufService.getStockDisponible();
        int nbOeufParJour = nombreDeJours * nombreOeufs;
        int nbOeufAvenir = nbOeufActuelle + nbOeufParJour;
        return nbOeufAvenir * prixUnitaire;
    }

    public int calculDate(Date dateFin) {
        Date aujourdHui = new Date(System.currentTimeMillis());
        long diffMs = dateFin.getTime() - aujourdHui.getTime();
        long joursCalendaires = (diffMs / 86400000L) + 1;
        int joursTravail = (int) ((joursCalendaires * 22) / 30);
        return joursTravail + 1;
    }

    
    public SimulationMortaliteResult simulateMortalite(Integer lotId, Integer raceId, int mortsParJour, int mortsParRace, Date dateSimulation) {
        
        Lot lot = lotRepository.findByIdWithRace(lotId)
                .orElseThrow(() -> new RuntimeException("Lot non trouvé"));

        
        Race raceLot = lot.getRace();
        if (raceLot == null) {
            throw new RuntimeException("Race non trouvée pour ce lot");
        }

        
        Race raceMorts = raceRepository.findById(raceId)
                .orElseThrow(() -> new RuntimeException("Race non trouvée"));

        
        int nombreDeJours = calculDate(dateSimulation);
        if (nombreDeJours <= 0) {
            nombreDeJours = 1;
        }
        
        
        Integer rendementMoyenMois = raceLot.getRendementMoyenMois();
        if (rendementMoyenMois == null || rendementMoyenMois <= 0) {
            throw new RuntimeException("Rendement moyen non défini pour cette race");
        }

        
        double oeufsParJourParPoule = rendementMoyenMois / 30.0;
        
        
        int populationInitiale = lot.getNombreInitial();
        
        
        int totalMorts = (mortsParJour * nombreDeJours) + mortsParRace;
        
               if (totalMorts > populationInitiale) {
            totalMorts = populationInitiale;
        }
        
        
        int populationRestante = populationInitiale - totalMorts;
        if (populationRestante < 0) {
            populationRestante = 0;
        }
        
        
        double populationMoyenne = (populationInitiale + populationRestante) / 2.0;
        int oeufsProduits = (int) (oeufsParJourParPoule * populationMoyenne * nombreDeJours);
        
        
        int nbOeufsRestants = oeufsProduits;
        if (nbOeufsRestants < 0) {
            nbOeufsRestants = 0;
        }
        
        
        int prixUnitaire = raceLot.getPrixUnitaire().intValue();
        if (prixUnitaire <= 0) {
            prixUnitaire = 100; 
        }
        
        
        BigDecimal chiffreAffaire = BigDecimal.valueOf(nbOeufsRestants * prixUnitaire);
        
                SimulationMortaliteResult result = new SimulationMortaliteResult(
                chiffreAffaire,
                nbOeufsRestants,
                totalMorts,
                raceMorts.getNom()
        );
        
        
        if (totalMorts == 0) {
            result.setMessage("Aucune mort simulée");
        } else if (totalMorts >= populationInitiale) {
            result.setMessage("Attention : Toutes les poules sont mortes !");
        }
        
        return result;
    }

    public BigDecimal getDepenseNourriture(LocalDate debut, LocalDate fin) {
        return mvtArgentRepository.sumDepensesByCategorieBetweenDates("achat_nourriture", debut, fin);
    }

    public BigDecimal getChiffreAffaires(LocalDate debut, LocalDate fin) {
        return financeService.getTotalRecettes(debut, fin);
    }

    public BigDecimal getBeneficeNet(LocalDate debut, LocalDate fin) {
        return financeService.getBeneficeNet(debut, fin);
    }
}