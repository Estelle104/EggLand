package com.app.eggland.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.eggland.model.Client;
import com.app.eggland.model.Lot;
import com.app.eggland.model.ProduitVente;
import com.app.eggland.service.ClientService;
import com.app.eggland.service.DetailVenteService;
import com.app.eggland.service.LotService;
import com.app.eggland.service.VenteService;

@Controller
public class VenteController {
    @Autowired
    private VenteService venteService;

    @Autowired
    private DetailVenteService detailVenteService;

    @Autowired
    private LotService lotService;

    @Autowired
    private ClientService clientService;
    
    @GetMapping("/ventes")
    public String afficherVentes() {
        return "redirect:/ventes/listevente";
    }

    @GetMapping("/ventes/listevente")
    public String listeVente(Model model) {
        model.addAttribute("ventes", venteService.listeVente());
        return "vente/listeVente";
    }

    @GetMapping("/ventes/creation")
    public String creationVente(Model model) {
        List<ProduitVente> produits = venteService.listeProduitVente();
        List<Lot> lots = lotService.getAllLots();
        List<Client> clients = clientService.listeClient();
        model.addAttribute("produits", produits);
        model.addAttribute("vente", new com.app.eggland.model.Vente());
        model.addAttribute("lots", lots);
        model.addAttribute("clients", clients);
        return "vente/formulairecreation";
    }

}
