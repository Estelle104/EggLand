package com.app.eggland.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.eggland.model.MvtStock;
import com.app.eggland.model.Nourriture;
import com.app.eggland.service.MvtStockService;
import com.app.eggland.service.NourritureService;
import com.app.eggland.service.PaginationUtils;

@Controller
@RequestMapping("/admin/stock")
public class MvtStockController {

    @Autowired
    private MvtStockService mvtStockService;

    @Autowired
    private NourritureService nourritureService;

    // Afficher la liste des stocks avec le stock actuel pour chaque nourriture
    @GetMapping
    public String liste(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model) {
        List<Nourriture> nourritures = nourritureService.findAll();

        Map<Integer, BigDecimal> stocks = new HashMap<>();
        for (Nourriture n : nourritures) {
            stocks.put(n.getId(), mvtStockService.calculerStockActuel(n.getId()));
        }
        Page<Nourriture> nourrituresPage = PaginationUtils.paginerListe(nourritures, page, size);
        String baseUrl = "/admin/stock";
        Map<String, String> filtres = new HashMap<>();
        LocalDate today = LocalDate.now();
        model.addAttribute("stocks", stocks);
        model.addAttribute("pageTitle", "Stock des nourritures");
        model.addAttribute("today", today.toString());
        model.addAttribute("todayMinus30", today.minusDays(30).toString());

        model.addAttribute("nourritures", nourrituresPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", nourrituresPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("filtres", filtres);
        model.addAttribute("baseUrl", baseUrl);
        return "stock/liste";
    }

    // Afficher le formulaire pour une entrée de stock
    @GetMapping("/entree")
    public String entreeForm(Model model) {
        model.addAttribute("mvtStock", MvtStock.builder()
                .date(LocalDate.now())
                .build());
        model.addAttribute("nourritures", nourritureService.findAll());
        model.addAttribute("pageTitle", "Entrée de stock");
        return "stock/entree";
    }

    // Enregistrer une entrée de stock
    @PostMapping("/entree")
    public String entreeSubmit(@RequestParam Integer nourriture,
            @RequestParam BigDecimal quantite, @RequestParam LocalDate date) {
        if (date.isAfter(LocalDate.now()))
            throw new StockException("La date ne peut pas être dans le futur", "/admin/stock/entree");
        if (quantite.compareTo(BigDecimal.ZERO) <= 0)
            throw new StockException("La quantité doit être supérieure à 0", "/admin/stock/entree");
        Nourriture n = nourritureService.findById(nourriture)
                .orElseThrow(() -> new StockException("Nourriture non trouvée", "/admin/stock/entree"));
        MvtStock mvtStock = MvtStock.builder()
                .nourriture(n)
                .type(mvtStockService.getTypeEntree())
                .quantite(quantite)
                .date(date)
                .build();
        mvtStockService.save(mvtStock);
        return "redirect:/admin/stock";
    }

    // Afficher le formulaire pour une sortie de stock
    @GetMapping("/sortie")
    public String sortieForm(Model model) {
        List<Nourriture> nourritures = nourritureService.findAll();
        Map<Integer, BigDecimal> stocks = new HashMap<>();
        for (Nourriture n : nourritures) {
            stocks.put(n.getId(), mvtStockService.calculerStockActuel(n.getId()));
        }
        model.addAttribute("nourritures", nourritures);
        model.addAttribute("stocks", stocks);
        model.addAttribute("pageTitle", "Sortie de stock");
        return "stock/sortie";
    }

    // Enregistrer une sortie de stock
    @PostMapping("/sortie")
    public String sortieSubmit(@RequestParam Integer nourriture,
            @RequestParam BigDecimal quantite, @RequestParam LocalDate date) {
        if (date.isAfter(LocalDate.now()))
            throw new StockException("La date ne peut pas être dans le futur", "/admin/stock/sortie");
        if (quantite.compareTo(BigDecimal.ZERO) <= 0)
            throw new StockException("La quantité doit être supérieure à 0", "/admin/stock/sortie");
        Nourriture n = nourritureService.findById(nourriture)
                .orElseThrow(() -> new StockException("Nourriture non trouvée", "/admin/stock/sortie"));
        BigDecimal stockActuel = mvtStockService.calculerStockActuel(nourriture);
        if (quantite.compareTo(stockActuel) > 0)
            throw new StockException("Stock insuffisant. Stock actuel : " + stockActuel + " kg", "/admin/stock/sortie");
        MvtStock mvtStock = MvtStock.builder()
                .nourriture(n)
                .type(mvtStockService.getTypeSortie())
                .quantite(quantite)
                .date(date)
                .build();
        mvtStockService.save(mvtStock);
        return "redirect:/admin/stock";
    }

    // Afficher l'historique des mouvements de stock avec filtres
    @GetMapping("/historique")
    public String historique(Model model,
            @RequestParam(required = false) Integer nourritureId,
            @RequestParam(required = false) String typeCode,
            @RequestParam(required = false) LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
        ) {

        List<MvtStock> mouvements;

        if (nourritureId != null) {
            mouvements = mvtStockService.findByNourritureId(nourritureId);
        } else {
            mouvements = mvtStockService.findAll();
        }

        if (typeCode != null && !typeCode.isEmpty()) {
            mouvements = mouvements.stream()
                    .filter(m -> m.getType().getCode().equalsIgnoreCase(typeCode))
                    .toList();
        }
        if (dateDebut != null) {
            mouvements = mouvements.stream()
                    .filter(m -> !m.getDate().isBefore(dateDebut))
                    .toList();
        }
        if (dateFin != null) {
            mouvements = mouvements.stream()
                    .filter(m -> !m.getDate().isAfter(dateFin))
                    .toList();
        }

        Page<MvtStock> mouvementsPage = PaginationUtils.paginerListe(mouvements, page, size);
        StringBuilder url = new StringBuilder("/admin/stock/historique?");
        if (nourritureId != null) url.append("nourritureId=").append(nourritureId).append("&");
        if (typeCode != null && !typeCode.isEmpty()) url.append("typeCode=").append(typeCode).append("&");
        if (dateDebut != null) url.append("dateDebut=").append(dateDebut).append("&");
        if (dateFin != null) url.append("dateFin=").append(dateFin).append("&");
        String baseUrl = url.toString();
        if (baseUrl.endsWith("&") || baseUrl.endsWith("?")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        Map<String, String> filtres = new HashMap<>();
        if (nourritureId != null) filtres.put("nourritureId", nourritureId.toString());
        if (typeCode != null && !typeCode.isEmpty()) filtres.put("typeCode", typeCode);
        if (dateDebut != null) filtres.put("dateDebut", dateDebut.toString());
        if (dateFin != null) filtres.put("dateFin", dateFin.toString());
        

        model.addAttribute("nourritures", nourritureService.findAll());
        model.addAttribute("pageTitle", "Historique des mouvements");

        model.addAttribute("mouvements", mouvementsPage.getContent());
        model.addAttribute("currentPage", mouvementsPage.getNumber());
        model.addAttribute("totalPages", mouvementsPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("filtres", filtres);
        return "stock/historique";
    }

    //Exception handler pour gérer les erreurs de stock et rediriger vers la page appropriée avec un message d'erreur
    @ExceptionHandler(StockException.class)
    public String handleStockException(StockException e, RedirectAttributes ra) {
        ra.addFlashAttribute("error", e.getMessage());
        return "redirect:" + e.getRedirectUrl();
    }

    private static class StockException extends RuntimeException {
        private final String redirectUrl;

        StockException(String message, String redirectUrl) {
            super(message);
            this.redirectUrl = redirectUrl;
        }

        String getRedirectUrl() {
            return redirectUrl;
        }
    }
}
