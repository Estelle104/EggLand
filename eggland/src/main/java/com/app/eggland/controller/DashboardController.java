package com.app.eggland.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.model.Lot;
import com.app.eggland.service.DashboardService;
import com.app.eggland.service.MortService;
import com.app.eggland.service.OeufProductionService;

@Controller
@RequestMapping("/admin/dashboard")
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

        Integer totalMorts = mortService.getTotalMorts(lotId, dateDebut, dateFin);
        Integer totalVivants = mortService.getVivants(lotId);
        Integer totalInitial = mortService.getTotalInitial(lotId);

        if (lotId != null && lotId > 0) {
            Integer totalMortsLot = mortService.getTotalMortsParLot(lotId);
            mortService.verifierSeuil(lotId, totalMortsLot);
        } else {
            List<Lot> lots = mortService.getAllLots();
            for (Lot lot : lots) {
                Integer totalMortsLot = mortService.getTotalMortsParLot(lot.getId());
                mortService.verifierSeuil(lot.getId(), totalMortsLot);
            }
        }

        model.addAttribute("oeufsJour", dashboardService.getOeufsJour());
        model.addAttribute("stockDisponible", dashboardService.getStockDisponible());
        model.addAttribute("ventesJour", dashboardService.getVentesJour());
        model.addAttribute("beneficeJour", dashboardService.getBeneficeJour());
        model.addAttribute("livraisonsEnCours", dashboardService.getLivraisonsEnCours());
        model.addAttribute("production14Jours", oeufProductionService.getProductionDes14DerniersJours());
        model.addAttribute("totalMorts", totalMorts);
        model.addAttribute("totalVivants", totalVivants);
        model.addAttribute("totalInitial", totalInitial);
        model.addAttribute("lots", mortService.getAllLots());
        model.addAttribute("lotIdSelectionne", lotId);
        model.addAttribute("dateDebutSelectionnee", dateDebut != null ? dateDebut.toString() : "");
        model.addAttribute("dateFinSelectionnee", dateFin != null ? dateFin.toString() : "");
        model.addAttribute("pageTitle", "Dashboard");
        return "dashboard/index";
    }
}
