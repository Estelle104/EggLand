package com.app.eggland.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.dto.SimulationMortaliteResult;
import com.app.eggland.model.Lot;
import com.app.eggland.model.Race;
import com.app.eggland.repository.LotRepository;
import com.app.eggland.repository.RaceRepository;
import com.app.eggland.service.SimulationService;

@Controller
@RequestMapping("/admin/simulation")
public class SimulationController {
    @Autowired
    private SimulationService simulationService;
    
    @Autowired
    private LotRepository lotRepository;
    
    @Autowired
    private RaceRepository raceRepository;

    @GetMapping({"", "/"})
    public String simulation(Model model) {
        model.addAttribute("dateDebut", LocalDate.now());
        
        
        List<Lot> lots = lotRepository.findActiveLotsWithRace();
        model.addAttribute("lots", lots);
        
        
        List<Race> races = raceRepository.findAll();
        model.addAttribute("races", races);
        
        return "simulation/simulation";
    }

    @PostMapping({"", "/"})
    public String runSimulation(Model model,
                               @RequestParam("date") Date dateFin,
                               @RequestParam("nbOeufs") int nombreOeufs,
                               @RequestParam("prixUnitaire") int prixUnitaire) {
        int resultat = simulationService.runSimulation(dateFin, nombreOeufs, prixUnitaire);
        model.addAttribute("resultat", resultat);
        
        
        List<Lot> lots = lotRepository.findActiveLotsWithRace();
        model.addAttribute("lots", lots);
        
        List<Race> races = raceRepository.findAll();
        model.addAttribute("races", races);
        model.addAttribute("dateDebut", LocalDate.now());
        
        return "simulation/simulation";
    }

    @PostMapping("/simulation/mortalite")
    public String runSimulationMortalite(Model model,
                                        @RequestParam("lotId") Integer lotId,
                                        @RequestParam("raceId") Integer raceId,
                                        @RequestParam("mortsParJour") int mortsParJour,
                                        @RequestParam("mortsParRace") int mortsParRace,
                                        @RequestParam("dateSimulation") Date dateSimulation) {
        try {
            
            SimulationMortaliteResult result = simulationService.simulateMortalite(
                    lotId, raceId, mortsParJour, mortsParRace, dateSimulation);
            
        
            model.addAttribute("resultatMortalite", result);
            
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur lors de la simulation: " + e.getMessage());
        }
        
        
        List<Lot> lots = lotRepository.findActiveLotsWithRace();
        model.addAttribute("lots", lots);
        
        List<Race> races = raceRepository.findAll();
        model.addAttribute("races", races);
        model.addAttribute("dateDebut", LocalDate.now());
        
        return "simulation/simulation";
    }

    @GetMapping("/chiffre-affaire")
    public String simulationChiffreAffaire(Model model) {
        model.addAttribute("dateDebut", LocalDate.now());
        return "simulation/chiffre-affaire";
    }

    @PostMapping("/chiffre-affaire")
    public String runSimulationChiffreAffaire(Model model,
                                             @RequestParam("dateDebut") LocalDate dateDebut,
                                             @RequestParam("dateFin") LocalDate dateFin) {
        LocalDate debut = dateDebut != null ? dateDebut : LocalDate.now();
        model.addAttribute("chiffreAffaires", simulationService.getChiffreAffaires(debut, dateFin));
        model.addAttribute("depenseNourriture", simulationService.getDepenseNourriture(debut, dateFin));
        model.addAttribute("beneficeNet", simulationService.getBeneficeNet(debut, dateFin));
        model.addAttribute("dateDebut", debut);
        model.addAttribute("dateFin", dateFin);
        
        return "simulation/chiffre-affaire";
    }
}