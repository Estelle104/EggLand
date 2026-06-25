package com.app.eggland.controller;

import com.app.eggland.model.Mort;
import com.app.eggland.service.MortService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/morts")
public class MortController {
    @Autowired
    private MortService mortService;

    @GetMapping
    public String liste(Model model) {
        List<Mort> morts = mortService.findAll();
        Map<Integer, Integer> currentCounts = new HashMap<>();
        for (Mort mort : morts) {
            currentCounts.put(mort.getId(), mortService.getNombreActuel(mort.getLot()));
        }

        model.addAttribute("morts", morts);
        model.addAttribute("currentCounts", currentCounts);
        model.addAttribute("pageTitle", "Liste des morts");
        return "morts/liste";
    }

    @GetMapping("/insertion")
    public String insertion(Model model) {
        model.addAttribute("mort", new Mort());
        model.addAttribute("lots", mortService.findAllLotTemporary());
        model.addAttribute("pageTitle", "Insertion de mort par lot");
        return "morts/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Mort mort) {
        mortService.save(mort);
        return "redirect:/morts";
    }

    @GetMapping("/historique")
    public String historique(Model model) {
        List<Mort> morts = mortService.findAll();
        int totalMorts = morts.stream()
                .mapToInt(m -> m.getNombre() == null ? 0 : m.getNombre())
                .sum();

        Map<Integer, Integer> currentCounts = new HashMap<>();
        for (Mort mort : morts) {
            currentCounts.put(mort.getId(), mortService.getNombreActuel(mort.getLot()));
        }

        model.addAttribute("morts", morts);
        model.addAttribute("currentCounts", currentCounts);
        model.addAttribute("lots", mortService.findAllLotTemporary());
        model.addAttribute("pageTitle", "Historique des mortalités");
        model.addAttribute("totalMorts", totalMorts);
        return "morts/liste";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Mort mort = mortService.findById(id)
                .orElseThrow(() -> new RuntimeException("Mort non trouvée"));
        model.addAttribute("mort", mort);
        model.addAttribute("lots", mortService.findAllLotTemporary());
        model.addAttribute("pageTitle", "Modifier la mort");
        return "morts/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            mortService.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Impossible de supprimer : cette mort est liée à d'autres données.");
        }
        return "redirect:/morts";
    }
}

