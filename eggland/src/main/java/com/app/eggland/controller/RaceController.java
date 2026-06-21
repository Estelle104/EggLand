package com.app.eggland.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.eggland.model.Race;
import com.app.eggland.service.RaceService;

@Controller
@RequestMapping("/races")
public class RaceController {
    @Autowired
    private RaceService raceService;

    //liste des races
    @GetMapping
    public String liste(Model model) {
        model.addAttribute("races", raceService.findAll());
        model.addAttribute("pageTitle", "Liste des races");
        return "races/liste";
    }

    //acceder au formulaire de création d'une nouvelle
    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("race", new Race());
        model.addAttribute("pageTitle", "Nouvelle race");
        return "races/form";
    }

    //enregistrer une race 
    @PostMapping("/save")
    public String save(@ModelAttribute Race race) {
        raceService.save(race);
        return "redirect:/races";
    }

    //modifier uen race
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Race race = raceService.findById(id)
                .orElseThrow(() -> new RuntimeException("Race non trouvée"));
        model.addAttribute("race", race);
        model.addAttribute("pageTitle", "Modifier la race");
        return "races/form";
    }

    //supprimer une race
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        raceService.deleteById(id);
        return "redirect:/races";
    }
}
