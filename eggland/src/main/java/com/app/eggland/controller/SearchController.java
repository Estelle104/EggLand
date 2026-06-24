package com.app.eggland.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.service.SearchService;
import com.app.eggland.service.SearchService.SearchResult;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    private static final Map<String, String> URL_MAP = new LinkedHashMap<>();

    static {
        URL_MAP.put("race", "/races");
        URL_MAP.put("batiment", "/batiments");
        URL_MAP.put("nourriture", "/nourritures");
        URL_MAP.put("employe", "/employes");
        URL_MAP.put("lot", "/lots");
        URL_MAP.put("client", "/client");
        URL_MAP.put("vente", "/ventes");
        URL_MAP.put("mvtargent", "/mvtargent");
        URL_MAP.put("mvtstock", "/stock");
        URL_MAP.put("configuration", "/configuration");
        URL_MAP.put("oeuf_production", "/oeufs");
        URL_MAP.put("traitement", "/traitements");
        URL_MAP.put("livraison", "/livraisons");
        URL_MAP.put("produit_vente", "/produits");
        URL_MAP.put("paiement_salaire", "/salaires");
        URL_MAP.put("versement_salaire", "/salaires");
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String query, Model model) {
        List<SearchResult> results = searchService.rechercher(query);

        Map<String, List<ResultatAvecUrl>> grouped = new LinkedHashMap<>();
        for (SearchResult r : results) {
            String url = URL_MAP.getOrDefault(r.table(), "/" + r.table() + "s");
            grouped.computeIfAbsent(r.table(), k -> new ArrayList<>())
                   .add(new ResultatAvecUrl(r.table(), r.column(), r.id(), r.valeur(), url));
        }

        int total = results.size();
        model.addAttribute("query", query);
        model.addAttribute("grouped", grouped);
        model.addAttribute("totalResultats", total);
        model.addAttribute("nbCategories", grouped.size());
        model.addAttribute("pageTitle", "Recherche : " + query);
        return "search/resultats";
    }

    public record ResultatAvecUrl(String table, String column, int id, String valeur, String url) {}
}
