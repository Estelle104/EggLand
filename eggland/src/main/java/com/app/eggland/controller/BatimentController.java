package com.app.eggland.controller;

import java.util.List;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.model.Batiment;
import com.app.eggland.service.BatimentService;

@Controller
@RequestMapping("/admin/batiments")
public class BatimentController {
    @Autowired
    private BatimentService batimentService;

    @GetMapping 
    public String liste(
        @RequestParam(defaultValue = "0")int page,
        @RequestParam(defaultValue = "10")int size,
        Model model) {
        List<Batiment> batiments = batimentService.findAll();
        // 2. On calcule le nombre total de pages manuellement
        int totalElements = batiments.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        if (totalPages == 0) totalPages = 1;
        
        List<Batiment> batimentsPage = batimentService.getPage(batiments, page, size);
        model.addAttribute("batiments", batimentsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("pageTitle", "Liste des bâtiments");
        return "batiments/liste";
    }


    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("batiment", new Batiment());
        model.addAttribute("pageTitle", "Nouveau bâtiment");
        return "batiments/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Batiment batiment) {
        batimentService.save(batiment);
        return "redirect:/admin/batiments";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Batiment batiment = batimentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Bâtiment non trouvé"));
        model.addAttribute("batiment", batiment);
        model.addAttribute("pageTitle", "Modifier le bâtiment");
        return "batiments/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            batimentService.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Impossible de supprimer : ce bâtiment est lié à des lots.");
        }
        return "redirect:/admin/batiments";
    }
}
