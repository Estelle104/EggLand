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

import com.app.eggland.model.Race;
import com.app.eggland.service.PaginationUtils;
import com.app.eggland.service.RaceService;

@Controller
@RequestMapping("/admin/races")
public class RaceController {
    @Autowired
    private RaceService raceService;

    @GetMapping
    public String liste(
        @RequestParam(defaultValue = "1")int page,
        @RequestParam(defaultValue = "10")int size,
        Model model) {
        List<Race> races = raceService.findAll();
        Page<Race> pageRaces = PaginationUtils.paginerListe(races, page, size);
        Map<String, String> filtres = Map.of(); // Pas de filtres pour l'instant
        model.addAttribute("races", pageRaces.getContent());
        model.addAttribute("currentPage", pageRaces.getNumber());
        model.addAttribute("totalPages", pageRaces.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("pageTitle", "Liste des races");
        model.addAttribute("filtres", filtres);
        return "races/liste";
    }


    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("race", new Race());
        model.addAttribute("pageTitle", "Nouvelle race");
        return "races/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Race race) {
        raceService.save(race);
        return "redirect:/admin/races";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Race race = raceService.findById(id)
                .orElseThrow(() -> new RuntimeException("Race non trouvée"));
        model.addAttribute("race", race);
        model.addAttribute("pageTitle", "Modifier la race");
        return "races/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            raceService.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Impossible de supprimer : cette race est liée à des lots.");
        }
        return "redirect:/admin/races";
    }
}
