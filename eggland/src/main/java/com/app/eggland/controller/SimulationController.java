package com.app.eggland.controller;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import com.app.eggland.service.SimulationService;


@Controller
public class SimulationController {
    @Autowired
    private SimulationService simulationService;

    @GetMapping("/admin/simulation")
    public String simulation(Model model) {
        return "simulation/simulation";
    }

    @PostMapping("/admin/simulation")
    public String runSimulation(Model model,
                                @RequestParam("date") Date dateFin,
                                @RequestParam("nbOeufs") int nombreOeufs,
                                @RequestParam("prixUnitaire") int prixUnitaire) {
        int resultat = simulationService.runSimulation(dateFin, nombreOeufs, prixUnitaire);
        model.addAttribute("resultat", resultat);
        return "simulation/simulation";
    }
}
