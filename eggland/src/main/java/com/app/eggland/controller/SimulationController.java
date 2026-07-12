package com.app.eggland.controller;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.dto.SimulationMortaliteResult;
import com.app.eggland.model.Lot;
import com.app.eggland.model.Race;
import com.app.eggland.repository.LotRepository;
import com.app.eggland.repository.RaceRepository;
import com.app.eggland.service.SimulationService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SimulationController {
    @Autowired
    private SimulationService simulationService;
    
    @Autowired
    private LotRepository lotRepository;
    
    @Autowired
    private RaceRepository raceRepository;

    @GetMapping("/admin/simulation")
    public String simulationAdmin(Model model) {
        return simulation(model);
    }

    @GetMapping("/simulation")
    public String simulation(Model model) {
        model.addAttribute("dateDebut", LocalDate.now());
        
        List<Lot> lots = lotRepository.findActiveLotsWithRace();
        model.addAttribute("lots", lots != null ? lots : List.of());
        
        List<Race> races = raceRepository.findAll();
        model.addAttribute("races", races != null ? races : List.of());
        
        return "simulation/simulation";
    }

    @GetMapping("admin/simulation/chiffre-affaire")
    public String chiffreAffaireForm(Model model) {
        model.addAttribute("dateDebut", LocalDate.now());
        return "redirect:/simulation";
    }

    @PostMapping("/simulation")
    public String runSimulation(Model model,
                               @RequestParam("date") Date dateFin,
                               @RequestParam("nbOeufs") int nombreOeufs,
                               @RequestParam("prixUnitaire") int prixUnitaire) {
        int resultat = simulationService.runSimulation(dateFin, nombreOeufs, prixUnitaire);
        model.addAttribute("resultat", resultat);
        
        // Recharger les données pour la simulation de mortalité
        model.addAttribute("lots", lotRepository.findActiveLotsWithRace());
        model.addAttribute("races", raceRepository.findAll());
        model.addAttribute("dateDebut", LocalDate.now());
        
        return "simulation/simulation";
    }

    @PostMapping("/simulation/mortalite")
    public String runSimulationMortalite(Model model,
                                        @RequestParam("lotId") Integer lotId,
                                        @RequestParam(value = "raceIds", required = false) List<Integer> raceIds,
                                        @RequestParam("dateSimulation") Date dateSimulation,
                                        HttpServletRequest request) {
        try {
            // Récupérer les nombres de morts pour chaque race sélectionnée
            Map<Integer, Integer> mortsParRace = new HashMap<>();
            
            if (raceIds != null && !raceIds.isEmpty()) {
                for (Integer raceId : raceIds) {
                    String paramName = "mortsParRace_" + raceId;
                    String mortsValue = request.getParameter(paramName);
                    if (mortsValue != null && !mortsValue.isEmpty()) {
                        int morts = Integer.parseInt(mortsValue);
                        if (morts > 0) {
                            mortsParRace.put(raceId, morts);
                        }
                    }
                }
            }
            
            // Vérifier qu'au moins une race est sélectionnée avec des morts > 0
            if (mortsParRace.isEmpty()) {
                model.addAttribute("erreur", "Veuillez cocher au moins une race et entrer un nombre de morts par jour supérieur à 0.");
            } else {
                // Exécuter la simulation de mortalité
                SimulationMortaliteResult result = simulationService.simulateMortalite(
                        lotId, mortsParRace, dateSimulation);
                model.addAttribute("resultatMortalite", result);
            }
            
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur lors de la simulation: " + e.getMessage());
        }
        
        // Recharger les données pour le formulaire
        model.addAttribute("lots", lotRepository.findActiveLotsWithRace());
        model.addAttribute("races", raceRepository.findAll());
        model.addAttribute("dateDebut", LocalDate.now());
        
        return "simulation/simulation";
    }

    @PostMapping("admin/simulation/chiffre-affaire")
    public String runSimulationChiffreAffaire(Model model,
                                             @RequestParam("dateDebut") LocalDate dateDebut,
                                             @RequestParam("dateFin") LocalDate dateFin) {
        LocalDate debut = dateDebut != null ? dateDebut : LocalDate.now();
        BigDecimal ca = simulationService.getChiffreAffaires(debut, dateFin);
        BigDecimal depenseNourriture = simulationService.getDepenseNourriture(debut, dateFin);
        BigDecimal beneficeNet = ca.subtract(depenseNourriture);
        model.addAttribute("chiffreAffaires", ca);
        model.addAttribute("depenseNourriture", depenseNourriture);
        model.addAttribute("beneficeNet", beneficeNet);
        model.addAttribute("dateDebut", debut);
        model.addAttribute("dateFin", dateFin);
        
        return "simulation/chiffre-affaire";
    }
}