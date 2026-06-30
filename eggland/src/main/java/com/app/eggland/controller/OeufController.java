package com.app.eggland.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.model.OeufProduction;
import com.app.eggland.service.LotService;
import com.app.eggland.service.OeufProductionService;
import com.app.eggland.service.OeufService;
import com.app.eggland.service.OeufStatutService;

import java.time.LocalDate;
import java.util.Map;


@Controller
@RequestMapping("/admin/oeufs")
public class OeufController {

    @Autowired
    private LotService lotService;

    @Autowired
    private OeufStatutService oeufStatutService;

    @Autowired
    private OeufProductionService oeufProductionService;

    @Autowired
    private OeufService oeufService;
    
    @GetMapping
    public String stats(Model model) {
        model.addAttribute("stock", oeufService.getStockDisponible());
        model.addAttribute("tauxParLot", oeufProductionService.getTauxPonteParLot());
        model.addAttribute("production14Jours", oeufProductionService.getProductionDes14DerniersJours());
        return "oeufs/stats";
    }

    @GetMapping("/saisie")
    public String saisie(Model model) {
        OeufProduction op = new OeufProduction();
        model.addAttribute("oeufProduction", op);
        model.addAttribute("listeLots", lotService.getAllLotsActifs());
        model.addAttribute("listeStatuts", oeufStatutService.getStatutsSaisissables());
        model.addAttribute("dateMax", LocalDate.now());
        return "oeufs/saisie";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute OeufProduction oeufProduction, Model model) {
        try {
            oeufProductionService.addOeufProduction(oeufProduction);
            return "redirect:/admin/oeufs";
        } catch (RuntimeException exception) {
            model.addAttribute("erreur", exception.getMessage());
            model.addAttribute("oeufProduction", oeufProduction);
            model.addAttribute("listeLots", lotService.getAllLotsActifs());
            model.addAttribute("listeStatuts", oeufStatutService.getStatutsSaisissables());
            model.addAttribute("dateMax", LocalDate.now());
            
            return "oeufs/saisie";
        }
    }

    @GetMapping("/historique")
    public String historique(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size, 
                            Model model) {
        page = Math.max(page, 0);
        size = Math.max(size, 1);
        Pageable pageable = PageRequest.of(page, size);

        Page<Map<String, Object>> historique = oeufProductionService.getHistoriqueProduction(pageable);
        if (historique.getTotalPages() > 0 && page >= historique.getTotalPages()) {
            page = historique.getTotalPages() - 1;
            pageable = PageRequest.of(page, size);
            historique = oeufProductionService.getHistoriqueProduction(pageable);
        }
        
        LocalDate today = LocalDate.now();
        model.addAttribute("historique", historique);
        model.addAttribute("historiqueProduction", historique.getContent());
        model.addAttribute("today", today.toString());
        model.addAttribute("todayMinus30", today.minusDays(30).toString());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", historique.getTotalPages());
        return "oeufs/historique";
    }
    
}
