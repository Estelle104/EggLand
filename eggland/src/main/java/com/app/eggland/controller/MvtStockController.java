package com.app.eggland.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.model.Configuration;
import com.app.eggland.model.MvtStock;
import com.app.eggland.model.Nourriture;
import com.app.eggland.repository.ConfigurationRepository;
import com.app.eggland.service.MvtStockService;
import com.app.eggland.service.NourritureService;

@Controller
@RequestMapping("/stock")
public class MvtStockController {

    @Autowired
    private MvtStockService mvtStockService;

    @Autowired
    private NourritureService nourritureService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @GetMapping
    public String liste(Model model) {
        Configuration config = configurationRepository.findById(1).orElse(null);
        List<Nourriture> nourritures = nourritureService.findAll();

        model.addAttribute("nourritures", nourritures);
        model.addAttribute("seuil", config != null ? config.getSeuilNourriture() : 50.0);
        model.addAttribute("pageTitle", "Stock des nourritures");
        return "stock/liste";
    }

    @GetMapping("/entree")
    public String entreeForm(Model model) {
        model.addAttribute("mvtStock", MvtStock.builder()
                .date(LocalDate.now())
                .build());
        model.addAttribute("nourritures", nourritureService.findAll());
        model.addAttribute("pageTitle", "Entrée de stock");
        return "stock/entree";
    }

    @PostMapping("/entree")
    public String entreeSubmit(@ModelAttribute MvtStock mvtStock) {
        mvtStock.setType(mvtStockService.getTypeEntree());
        mvtStockService.save(mvtStock);
        return "redirect:/stock";
    }

    @GetMapping("/sortie")
    public String sortieForm(Model model) {
        model.addAttribute("mvtStock", MvtStock.builder()
                .date(LocalDate.now())
                .build());
        model.addAttribute("nourritures", nourritureService.findAll());
        model.addAttribute("pageTitle", "Sortie de stock");
        return "stock/sortie";
    }

    @PostMapping("/sortie")
    public String sortieSubmit(@ModelAttribute MvtStock mvtStock) {
        mvtStock.setType(mvtStockService.getTypeSortie());
        mvtStockService.save(mvtStock);
        return "redirect:/stock";
    }

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
