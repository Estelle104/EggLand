package com.app.eggland.controller;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.model.MvtArgent;
import com.app.eggland.repository.MvtArgentRepository;
import com.app.eggland.repository.TypeMvtRepository;
import com.app.eggland.service.PaginationUtils;

@Controller
@RequestMapping("/admin/mvtargent")
public class MvtArgentController {

    @Autowired
    private MvtArgentRepository mvtArgentRepository;

    @Autowired
    private TypeMvtRepository typeMvtRepository;

    @GetMapping
    public String liste(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String typeCode,
        @RequestParam(required = false) String categorie,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
        Model model
    ) {
        typeCode = normalize(typeCode);
        categorie = normalize(categorie);

        List<MvtArgent> mouvements = mvtArgentRepository.findByFiltres(typeCode, categorie, dateDebut, dateFin);
        Page<MvtArgent> mouvementsPage = PaginationUtils.paginerListe(mouvements, page, size);
        String baseUrl = "/admin/mvtargent";
        model.addAttribute("pageTitle", "Mouvements d'argent");
        Map<String, String> filtres = new LinkedHashMap<>();
        addFilter(filtres, "typeCode", typeCode);
        addFilter(filtres, "categorie", categorie);
        addFilter(filtres, "dateDebut", dateDebut);
        addFilter(filtres, "dateFin", dateFin);

        model.addAttribute("mouvements", mouvementsPage.getContent());
        model.addAttribute("currentPage", mouvementsPage.getNumber());
        model.addAttribute("totalPages", mouvementsPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("filtres", filtres);
        model.addAttribute("typesMvt", typeMvtRepository.findAll());
        model.addAttribute("categories", mvtArgentRepository.findDistinctCategories());
        model.addAttribute("typeCodeSelectionne", typeCode);
        model.addAttribute("categorieSelectionnee", categorie);
        model.addAttribute("dateDebutSelectionnee", dateDebut);
        model.addAttribute("dateFinSelectionnee", dateFin);
        return "mvtargent/liste";
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void addFilter(Map<String, String> filtres, String key, Object value) {
        if (value != null && !value.toString().isBlank()) {
            filtres.put(key, value.toString());
        }
    }
}
