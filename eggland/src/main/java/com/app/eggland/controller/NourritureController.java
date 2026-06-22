package com.app.eggland.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.eggland.model.Nourriture;
import com.app.eggland.service.NourritureService;

@Controller
@RequestMapping("/nourritures")
public class NourritureController {

    @Autowired
    private NourritureService nourritureService;

    // Afficher la liste des nourritures
    @GetMapping
    public String liste(Model model) {
        model.addAttribute("nourritures", nourritureService.findAll());
        model.addAttribute("pageTitle", "Liste des nourritures");
        return "nourritures/liste";
    }

    // Afficher le formulaire pour créer une nouvelle nourriture
    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("nourriture", new Nourriture());
        model.addAttribute("pageTitle", "Nouvelle nourriture");
        return "nourritures/form";
    }

    // Enregistrer une nourriture (nouvelle ou modifiée)
    @PostMapping("/save")
    public String save(@ModelAttribute Nourriture nourriture) {
        nourritureService.save(nourriture);
        return "redirect:/nourritures";
    }

    // Modifier une nourriture existante
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Nourriture nourriture = nourritureService.findById(id)
                .orElseThrow(() -> new RuntimeException("Nourriture non trouvée"));
        model.addAttribute("nourriture", nourriture);
        model.addAttribute("pageTitle", "Modifier la nourriture");
        return "nourritures/form";
    }

    // Supprimer une nourriture
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        nourritureService.deleteById(id);
        return "redirect:/nourritures";
    }
}
