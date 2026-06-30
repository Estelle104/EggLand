package com.app.eggland.controller;

import com.app.eggland.service.DashboardService;
import com.app.eggland.service.OeufProductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private OeufProductionService oeufProductionService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("oeufsJour", dashboardService.getOeufsJour());
        model.addAttribute("stockDisponible", dashboardService.getStockDisponible());
        model.addAttribute("ventesJour", dashboardService.getVentesJour());
        model.addAttribute("beneficeMois", dashboardService.getBeneficeMois());
        model.addAttribute("livraisonsEnCours", dashboardService.getLivraisonsEnCours());
        model.addAttribute("production14Jours", oeufProductionService.getProductionDes14DerniersJours());
        model.addAttribute("pageTitle", "Dashboard");
        return "dashboard/index";
    }
}
