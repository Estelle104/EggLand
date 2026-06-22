package com.app.eggland.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.eggland.model.Batiment;
import com.app.eggland.service.BatimentService;

@Controller
@RequestMapping("/batiments")
public class BatimentController {
    @Autowired
    private BatimentService batimentService;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("batiments", batimentService.findAll());
        model.addAttribute("pageTitle", "Liste des bâtiments");
        return "batiments/liste";
    }

    // Afficher le formulaire pour créer un nouveau bâtiment
    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("batiment", new Batiment());
        model.addAttribute("pageTitle", "Nouveau bâtiment");
        return "batiments/form";
    }

    // Enregistrer un bâtiment (nouveau ou modifié)
    @PostMapping("/save")
    public String save(@ModelAttribute Batiment batiment) {
        batimentService.save(batiment);
        return "redirect:/batiments";
    }

    // Modifier un bâtiment existant
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Batiment batiment = batimentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Bâtiment non trouvé"));
        model.addAttribute("batiment", batiment);
        model.addAttribute("pageTitle", "Modifier le bâtiment");
        return "batiments/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        batimentService.deleteById(id);
        return "redirect:/batiments";
    }
}
