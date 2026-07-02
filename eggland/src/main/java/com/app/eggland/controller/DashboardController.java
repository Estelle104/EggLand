package com.app.eggland.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.service.DashboardService;
import com.app.eggland.service.MortService;
import com.app.eggland.service.OeufProductionService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private OeufProductionService oeufProductionService;

    @Autowired
    private MortService mortService;

    @GetMapping
    public String index(
            @RequestParam(value = "lotId", required = false) Integer lotId,
            @RequestParam(value = "dateDebut", required = false) LocalDate dateDebut,
            @RequestParam(value = "dateFin", required = false) LocalDate dateFin,
            Model model) {

        if (dateDebut == null) dateDebut = LocalDate.now().minusDays(30);
        if (dateFin == null) dateFin = LocalDate.now();

        Integer totalMorts = mortService.getTotalMorts(lotId, dateDebut, dateFin);
        Integer totalVivants = mortService.getVivants(lotId);

        if (lotId != null && lotId > 0) {
            mortService.verifierSeuil(lotId, totalMorts);
        }

        model.addAttribute("oeufsJour", dashboardService.getOeufsJour());
        model.addAttribute("stockDisponible", dashboardService.getStockDisponible());
        model.addAttribute("ventesJour", dashboardService.getVentesJour());
        model.addAttribute("beneficeJour", dashboardService.getBeneficeJour());
        model.addAttribute("livraisonsEnCours", dashboardService.getLivraisonsEnCours());
        model.addAttribute("production14Jours", oeufProductionService.getProductionDes14DerniersJours());
        model.addAttribute("totalMorts", totalMorts);
        model.addAttribute("totalVivants", totalVivants);
        model.addAttribute("lots", mortService.getAllLots());
        model.addAttribute("lotIdSelectionne", lotId);
        model.addAttribute("dateDebutSelectionnee", dateDebut.toString());
        model.addAttribute("dateFinSelectionnee", dateFin.toString());
        model.addAttribute("pageTitle", "Dashboard");
        return "dashboard/index";
    }
}
