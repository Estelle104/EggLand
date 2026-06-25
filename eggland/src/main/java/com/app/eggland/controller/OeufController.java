package com.app.eggland.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.eggland.model.OeufProduction;
import com.app.eggland.service.LotService;
import com.app.eggland.service.OeufProductionService;
import com.app.eggland.service.OeufService;
import com.app.eggland.service.OeufStatutService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/oeufs")
public class OeufController {

    @Autowired
    private LotService lotService;

    @Autowired
    private OeufStatutService oeufStatutService;

    @Autowired
    private OeufProductionService oeufProductionService;

    @Autowired
    private OeufService oeufService;
    
    @GetMapping
    public String stats(Model model) {
        model.addAttribute("stock", oeufService.getStockDisponible());
        model.addAttribute("tauxParLot", oeufProductionService.getTauxPonteParLot());
        model.addAttribute("production14Jours", oeufProductionService.getProductionDes14DerniersJours());
        return "oeufs/stats";
    }

    @GetMapping("/saisie")
    public String saisie(Model model) {
        OeufProduction op = new OeufProduction();
        model.addAttribute("oeufProduction", op);
        model.addAttribute("listeLots", lotService.getAllLotsActifs());
        model.addAttribute("listeStatuts", oeufStatutService.getStatutsSaisissables());
        model.addAttribute("dateMax", LocalDate.now());
        return "oeufs/saisie";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute OeufProduction oeufProduction, Model model) {
        try {
            oeufProductionService.addOeufProduction(oeufProduction);
            return "redirect:/oeufs";
        } catch (RuntimeException exception) {
            model.addAttribute("erreur", exception.getMessage());
            model.addAttribute("oeufProduction", oeufProduction);
            model.addAttribute("listeLots", lotService.getAllLotsActifs());
            model.addAttribute("listeStatuts", oeufStatutService.getStatutsSaisissables());
            model.addAttribute("dateMax", LocalDate.now());
            
            return "oeufs/saisie";
        }
    }
}
