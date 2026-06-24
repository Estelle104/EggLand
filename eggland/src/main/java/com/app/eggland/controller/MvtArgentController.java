package com.app.eggland.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.eggland.repository.MvtArgentRepository;

@Controller
@RequestMapping("/mvtargent")
public class MvtArgentController {

    @Autowired
    private MvtArgentRepository mvtArgentRepository;

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("mouvements", mvtArgentRepository.findAllByOrderByDateDesc());
        model.addAttribute("pageTitle", "Mouvements d'argent");
        return "mvtargent/liste";
    }
}
