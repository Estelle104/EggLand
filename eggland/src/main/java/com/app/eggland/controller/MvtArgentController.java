package com.app.eggland.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.model.MvtArgent;
import com.app.eggland.repository.MvtArgentRepository;
import com.app.eggland.service.PaginationUtils;

@Controller
@RequestMapping("/admin/mvtargent")
public class MvtArgentController {

    @Autowired
    private MvtArgentRepository mvtArgentRepository;

    @GetMapping
    public String liste(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model
    ) {
        List<MvtArgent> mouvements = mvtArgentRepository.findAllByOrderByDateDesc();
        Page<MvtArgent> mouvementsPage = PaginationUtils.paginerListe(mouvements, page, size);
        String baseUrl = "/admin/mvtargent";
        model.addAttribute("pageTitle", "Mouvements d'argent");
        Map<String, String> filtres = Map.of(); // Aucun filtre pour l'instant

        model.addAttribute("mouvements", mouvementsPage.getContent());
        model.addAttribute("currentPage", mouvementsPage.getNumber());
        model.addAttribute("totalPages", mouvementsPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("filtres", filtres);
        return "mvtargent/liste";
    }
}
