package com.app.eggland.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.model.MvtStock;
import com.app.eggland.model.Nourriture;
import com.app.eggland.service.MvtStockService;
import com.app.eggland.service.NourritureService;

@Controller
@RequestMapping("/stock")
public class MvtStockController {

    @Autowired
    private MvtStockService mvtStockService;

    @Autowired
    private NourritureService nourritureService;

    // Afficher la liste des stocks avec le stock actuel pour chaque nourriture
    @GetMapping
    public String liste(Model model) {
        List<Nourriture> nourritures = nourritureService.findAll();

        Map<Integer, BigDecimal> stocks = new HashMap<>();
        for (Nourriture n : nourritures) {
            stocks.put(n.getId(), mvtStockService.calculerStockActuel(n.getId()));
        }

        LocalDate today = LocalDate.now();
        model.addAttribute("nourritures", nourritures);
        model.addAttribute("stocks", stocks);
        model.addAttribute("pageTitle", "Stock des nourritures");
        model.addAttribute("today", today.toString());
        model.addAttribute("todayMinus30", today.minusDays(30).toString());
        return "stock/liste";
    }

    // Afficher le formulaire pour une entrée de stock
    @GetMapping("/entree")
    public String entreeForm(Model model) {
        model.addAttribute("mvtStock", MvtStock.builder()
                .date(LocalDate.now())
                .build());
        model.addAttribute("nourritures", nourritureService.findAll());
        model.addAttribute("pageTitle", "Entrée de stock");
        return "stock/entree";
    }

    // Enregistrer une entrée de stock
    @PostMapping("/entree")
    public String entreeSubmit(@RequestParam Integer nourriture,
            @RequestParam BigDecimal quantite, @RequestParam LocalDate date) {
        Nourriture n = nourritureService.findById(nourriture)
                .orElseThrow(() -> new RuntimeException("Nourriture non trouvée"));
        MvtStock mvtStock = MvtStock.builder()
                .nourriture(n)
                .type(mvtStockService.getTypeEntree())
                .quantite(quantite)
                .date(date)
                .build();
        mvtStockService.save(mvtStock);
        return "redirect:/stock";
    }

    // Afficher le formulaire pour une sortie de stock
    @GetMapping("/sortie")
    public String sortieForm(Model model) {
        model.addAttribute("nourritures", nourritureService.findAll());
        model.addAttribute("pageTitle", "Sortie de stock");
        return "stock/sortie";
    }

    // Enregistrer une sortie de stock
    @PostMapping("/sortie")
    public String sortieSubmit(@RequestParam Integer nourriture,
            @RequestParam BigDecimal quantite, @RequestParam LocalDate date) {
        Nourriture n = nourritureService.findById(nourriture)
                .orElseThrow(() -> new RuntimeException("Nourriture non trouvée"));
        MvtStock mvtStock = MvtStock.builder()
                .nourriture(n)
                .type(mvtStockService.getTypeSortie())
                .quantite(quantite)
                .date(date)
                .build();
        mvtStockService.save(mvtStock);
        return "redirect:/stock";
    }

    // Afficher l'historique des mouvements de stock avec filtres
    @GetMapping("/historique")
    public String historique(Model model,
            @RequestParam(required = false) Integer nourritureId,
            @RequestParam(required = false) String typeCode,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin) {

        List<MvtStock> mouvements;

        if (nourritureId != null) {
            mouvements = mvtStockService.findByNourritureId(nourritureId);
        } else {
            mouvements = mvtStockService.findAll();
        }

        if (typeCode != null && !typeCode.isEmpty()) {
            mouvements = mouvements.stream()
                    .filter(m -> m.getType().getCode().equals(typeCode))
                    .toList();
        }
        if (dateDebut != null) {
            mouvements = mouvements.stream()
                    .filter(m -> !m.getDate().isBefore(dateDebut))
                    .toList();
        }
        if (dateFin != null) {
            mouvements = mouvements.stream()
                    .filter(m -> !m.getDate().isAfter(dateFin))
                    .toList();
        }

        model.addAttribute("mouvements", mouvements);
        model.addAttribute("nourritures", nourritureService.findAll());
        model.addAttribute("pageTitle", "Historique des mouvements");
        return "stock/historique";
    }
}
