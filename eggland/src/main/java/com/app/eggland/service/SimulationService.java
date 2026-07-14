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
import com.app.eggland.model.LotRace;
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
    @Autowired
    private MortService mortService;

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
    public SimulationMortaliteResult simulateMortalite(Integer lotId,
                                                        Map<Integer, Integer> mortsParRace,Date dateSimulation) {

        Lot lot = lotRepository.findByIdWithRace(lotId)
                .orElseThrow(() -> new RuntimeException("Lot non trouvé"));

        int nombreDeJours = calculDate(dateSimulation);

        Map<String, Integer> mortsDetails = new HashMap<>();

        int totalMorts = 0;
        int totalOeufs = 0;

        BigDecimal chiffreAffaire = BigDecimal.ZERO;

        for (LotRace lotRace : lot.getLotRaces()) {

            Race race = lotRace.getRace();

            if (race == null) {
                continue;
            }

            int populationInitiale = mortService.getNombreActuel(lot, race);

            int mortsParJour = mortsParRace.getOrDefault(race.getId(), 0);

            int mortsSimulation = mortsParJour * nombreDeJours;

            if (mortsSimulation > populationInitiale) {
                mortsSimulation = populationInitiale;
            }

            mortsDetails.put(race.getNom(), mortsSimulation);

            totalMorts += mortsSimulation;

            int populationRestante = populationInitiale - mortsSimulation;

            double populationMoyenne = (populationInitiale + populationRestante) / 2.0;

            if (race.getRendementMoyenMois() == null)
                continue;

            double oeufsParJour = race.getRendementMoyenMois() / 30.0;

            int productionRace = (int) Math.round(
                    oeufsParJour *
                            populationMoyenne *
                            nombreDeJours);

            totalOeufs += productionRace;

            if (race.getPrixUnitaire() == null)
                continue;

            BigDecimal caRace = BigDecimal.valueOf(productionRace)
                    .multiply(race.getPrixUnitaire());

            chiffreAffaire = chiffreAffaire.add(caRace);
        }

        SimulationMortaliteResult result = new SimulationMortaliteResult(
                chiffreAffaire,
                totalOeufs,
                totalMorts,
                mortsDetails);

        if (totalMorts == 0) {
            result.setMessage("Aucune mort simulée");
        } else {

            int populationTotale = mortService.getNombreActuel(lot);

            if (totalMorts >= populationTotale) {
                result.setMessage("Attention : toutes les poules sont mortes !");
            }
        }

        return result;
    }

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