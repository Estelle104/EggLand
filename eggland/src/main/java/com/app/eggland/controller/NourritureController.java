package com.app.eggland.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.eggland.model.Nourriture;
import com.app.eggland.service.NourritureService;
import com.app.eggland.service.PaginationUtils;

@Controller
@RequestMapping("/admin/nourritures")
public class NourritureController {

    @Autowired
    private NourritureService nourritureService;

    @GetMapping
    public String liste(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(required = false) String keyword,
        Model model) {
        List<Nourriture> nourritures;
        if (keyword != null && !keyword.isEmpty()) {
            nourritures = nourritureService.findByLibelleContaining(keyword);
        } else {
            nourritures = nourritureService.findAll();
        }
        Page<Nourriture> nourriturePage = PaginationUtils.paginerListe(nourritures, page, size);
        Map<String, String> filtres = keyword != null ? Map.of("keyword", keyword) : Map.of();
        model.addAttribute("nourritures", nourriturePage.getContent());
        model.addAttribute("pageTitle", "Liste des nourritures");
        model.addAttribute("currentPage", nourriturePage.getNumber());
        model.addAttribute("totalPages", nourriturePage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("filtres", filtres);
        model.addAttribute("baseUrl", "/admin/nourritures");
        model.addAttribute("keyword", keyword);
        return "nourritures/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("nourriture", new Nourriture());
        model.addAttribute("pageTitle", "Nouvelle nourriture");
        return "nourritures/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Nourriture nourriture) {
        nourritureService.save(nourriture);
        return "redirect:/admin/nourritures";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Nourriture nourriture = nourritureService.findById(id)
                .orElseThrow(() -> new RuntimeException("Nourriture non trouvée"));
        model.addAttribute("nourriture", nourriture);
        model.addAttribute("pageTitle", "Modifier la nourriture");
        return "nourritures/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            nourritureService.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Impossible de supprimer : cette nourriture est liée à des mouvements de stock.");
        }
        return "redirect:/admin/nourritures";
    }
}
