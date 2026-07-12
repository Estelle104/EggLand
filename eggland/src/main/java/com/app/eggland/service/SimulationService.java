package com.app.eggland.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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

        int jours = (int) (diffMs / 86400000L) + 1;

        return Math.max(jours, 1);
    }

    // Méthode pour la simulation de mortalité
    public SimulationMortaliteResult simulateMortalite(Integer lotId, Map<Integer, Integer> mortsParRace,
            Date dateSimulation) {
        // Récupérer le lot
        Lot lot = lotRepository.findByIdWithRace(lotId)
                .orElseThrow(() -> new RuntimeException("Lot non trouvé"));

        // Récupérer la race du lot
        Race raceLot = lot.getRace();
        if (raceLot == null) {
            throw new RuntimeException("Race non trouvée pour ce lot");
        }

        // Calculer le nombre de jours
        int nombreDeJours = calculDate(dateSimulation);
        if (nombreDeJours <= 0) {
            nombreDeJours = 1;
        }

        // Récupérer le rendement
        Integer rendementMoyenMois = raceLot.getRendementMoyenMois();
        if (rendementMoyenMois == null || rendementMoyenMois <= 0) {
            throw new RuntimeException("Rendement moyen non défini pour cette race");
        }

        // Calculer la production de base
        double oeufsParJourParPoule = rendementMoyenMois / 30.0;
        int populationInitiale = lot.getNombreInitial();

        // Calculer le total des morts
        int totalMorts = 0;
        Map<String, Integer> mortsDetails = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : mortsParRace.entrySet()) {
            Integer raceId = entry.getKey();
            Integer mortsParJour = entry.getValue();

            // Calculer le nombre total de morts pour cette race sur la période
            int totalMortsRace = mortsParJour * nombreDeJours;

            // Récupérer le nom de la race
            Race race = raceRepository.findById(raceId)
                    .orElseThrow(() -> new RuntimeException("Race non trouvée: " + raceId));

            totalMorts += totalMortsRace;
            mortsDetails.put(race.getNom(), totalMortsRace);
        }

        // Limiter les morts à la population
        if (totalMorts > populationInitiale) {
            totalMorts = populationInitiale;
        }

        // Population restante
        int populationRestante = populationInitiale - totalMorts;
        if (populationRestante < 0) {
            populationRestante = 0;
        }

        // Calcul des œufs produits
        double populationMoyenne = (populationInitiale + populationRestante) / 2.0;
        int oeufsProduits = (int) (oeufsParJourParPoule * populationMoyenne * nombreDeJours);
        int nbOeufsRestants = Math.max(oeufsProduits, 0);

        // Calcul du chiffre d'affaires
        int prixUnitaire = raceLot.getPrixUnitaire().intValue();
        if (prixUnitaire <= 0) {
            prixUnitaire = 100;
        }
        BigDecimal chiffreAffaire = BigDecimal.valueOf(nbOeufsRestants * prixUnitaire);

        // Création du résultat
        SimulationMortaliteResult result = new SimulationMortaliteResult(
                chiffreAffaire, nbOeufsRestants, totalMorts, mortsDetails);

        if (totalMorts == 0) {
            result.setMessage("Aucune mort simulée");
        } else if (totalMorts >= populationInitiale) {
            result.setMessage("Attention : Toutes les poules sont mortes !");
        }

        return result;
    }

    // ===== MÉTHODES MANQUANTES À AJOUTER =====

    /**
     * Récupère le chiffre d'affaires entre deux dates
     */
    public BigDecimal getChiffreAffaires(LocalDate debut, LocalDate fin) {
        return financeService.getTotalRecettes(debut, fin);
    }

    /**
     * Récupère les dépenses de nourriture entre deux dates
     */
    public BigDecimal getDepenseNourriture(LocalDate debut, LocalDate fin) {
        return mvtArgentRepository.sumDepensesByCategorieBetweenDates("achat_nourriture", debut, fin);
    }

    /**
     * Récupère le bénéfice net entre deux dates
     */
    public BigDecimal getBeneficeNet(LocalDate debut, LocalDate fin) {
        return financeService.getBeneficeNet(debut, fin);
    }
}