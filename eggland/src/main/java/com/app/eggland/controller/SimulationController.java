package com.app.eggland.controller;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import com.app.eggland.service.SimulationService;


@Controller
@RequestMapping("/admin/simulation")
public class SimulationController {
    @Autowired
    private SimulationService simulationService;

    @GetMapping({"", "/"})
    public String simulation(Model model) {
        model.addAttribute("dateDebut", LocalDate.now());
        return "simulation/simulation";
    }

    @PostMapping({"", "/"})
    public String runSimulation(Model model,
                                @RequestParam("date") Date dateFin,
                                @RequestParam("nbOeufs") int nombreOeufs,
                                @RequestParam("prixUnitaire") int prixUnitaire) {
        int resultat = simulationService.runSimulation(dateFin, nombreOeufs, prixUnitaire);
        model.addAttribute("resultat", resultat);
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
