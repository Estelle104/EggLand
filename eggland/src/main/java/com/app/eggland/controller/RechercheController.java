package com.app.eggland.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.service.RechercheService;
import com.app.eggland.service.RechercheService.RechercheResultat;

@Controller
public class RechercheController {

    @Autowired
    private RechercheService rechercheService;

    private static final Map<String, String> URL_MAP = new LinkedHashMap<>();

    static {
        URL_MAP.put("race", "/races");
        URL_MAP.put("batiment", "/batiments");
        URL_MAP.put("nourriture", "/nourritures");
        URL_MAP.put("employe", "/employes");
        URL_MAP.put("lot", "/admin/lots");
        URL_MAP.put("vente", "/ventes");
        URL_MAP.put("mvtargent", "/admin/mvtargent");
        URL_MAP.put("mvtstock", "/stock");
        URL_MAP.put("oeuf_production", "/admin/oeufs");
        URL_MAP.put("livraison", "/admin/livraisons");
        URL_MAP.put("produit_vente", "/ventes");
        URL_MAP.put("paiement_salaire", "/employes");
        URL_MAP.put("versement_salaire", "/employes");
    }

    @GetMapping("/recherche")
    public String recherche(@RequestParam("q") String query, Model model, Authentication auth) {
        List<RechercheResultat> results = rechercheService.rechercher(query);

        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("admin"));

        Map<String, List<ResultatAvecUrl>> grouped = new LinkedHashMap<>();
        for (RechercheResultat r : results) {
            String url = URL_MAP.get(r.table());
            if (url != null && !isAdmin && (url.startsWith("/admin/") || url.startsWith("/client/"))) {
                url = null;
            }
            grouped.computeIfAbsent(r.table(), k -> new ArrayList<>())
                   .add(new ResultatAvecUrl(r.table(), r.column(), r.id(), r.valeur(), url));
        }

        int total = results.size();
        model.addAttribute("query", query);
        model.addAttribute("grouped", grouped);
        model.addAttribute("totalResultats", total);
        model.addAttribute("nbCategories", grouped.size());
        model.addAttribute("pageTitle", "Recherche : " + query);
        return "recherche/resultats";
    }

    public record ResultatAvecUrl(String table, String column, int id, String valeur, String url) {}
}
