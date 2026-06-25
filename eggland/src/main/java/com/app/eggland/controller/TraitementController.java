package com.app.eggland.controller;

import com.app.eggland.model.Traitement;
import com.app.eggland.service.TraitementService;
import com.app.eggland.service.LotService;
import com.app.eggland.service.TypeTraitementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/traitements")
public class TraitementController {
    @Autowired
    private TraitementService traitementService;

    @Autowired
    private LotService lotService;

    @Autowired
    private TypeTraitementService typeTraitementService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("traitements", traitementService.findAll());
        model.addAttribute("pageTitle", "Liste des traitements");
        return "traitements/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("traitement", new Traitement());
        model.addAttribute("lots", lotService.findAll());
        model.addAttribute("types", typeTraitementService.findAll());
        model.addAttribute("pageTitle", "Nouveau traitement");
        return "traitements/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Traitement traitement) {
        traitementService.save(traitement);
        return "redirect:/traitements";
    }

    @GetMapping("/historique")
    public String historique(Model model) {
        List<Traitement> traitements = traitementService.findAll();
        BigDecimal totalCost = traitements.stream()
                .map(Traitement::getCout)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgCost = traitements.isEmpty() ? BigDecimal.ZERO : totalCost.divide(BigDecimal.valueOf(traitements.size()), 2, java.math.RoundingMode.HALF_UP);

        model.addAttribute("traitements", traitements);
        model.addAttribute("lots", lotService.findAll());
        model.addAttribute("types", typeTraitementService.findAll());
        model.addAttribute("pageTitle", "Historique traitements");
        model.addAttribute("totalCost", totalCost);
        model.addAttribute("avgCost", avgCost);
        return "traitements/historique";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Traitement traitement = traitementService.findById(id)
                .orElseThrow(() -> new RuntimeException("Traitement non trouvé"));
        model.addAttribute("traitement", traitement);
        model.addAttribute("lots", lotService.findAll());
        model.addAttribute("types", typeTraitementService.findAll());
        model.addAttribute("pageTitle", "Modifier le traitement");
        return "traitements/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            traitementService.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Impossible de supprimer : ce traitement est lié à d'autres données.");
        }
        return "redirect:/traitements";
    }
}
