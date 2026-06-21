package com.app.eggland.controller;

import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.eggland.service.BatimentService;

@Controller
@RequestMapping("/batiments")
public class BatimentController {
    @Autowired
    private BatimentService batimentService;

    @GetMapping
    public String liste(Model model){
        model.addAttribute("batiments", batimentService.findAll());
        model.addAttribute("pageTitle", "Liste des bâtiments");
        return "batiments/liste";
    }

}
