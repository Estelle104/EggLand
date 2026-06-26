package com.app.eggland.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.eggland.service.DetailVenteService;
import com.app.eggland.service.VenteService;

@Controller
public class VenteController {
    @Autowired
    private VenteService venteService;

    @Autowired
    private DetailVenteService detailVenteService;

    @GetMapping("/ventes")
    public String afficherVentes() {
        return "redirect:/ventes/listevente";
    }

    @GetMapping("/ventes/listevente")
    public String listeVente(Model model) {
        model.addAttribute("ventes", venteService.listeVente());
        return "vente/listeVente";
    }

}
