package com.app.eggland.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.eggland.model.Client;
import com.app.eggland.model.Livraison;
import com.app.eggland.model.Vente;
import com.app.eggland.repository.DetailVenteRepository;
import com.app.eggland.repository.StatutLivraisonRepository;
import com.app.eggland.service.ClientService;
import com.app.eggland.service.LivraisonService;
import com.app.eggland.service.PaginationUtils;
import com.app.eggland.service.VenteService;

@Controller
@RequestMapping("/admin/livraisons")
public class LivraisonController {

    @Autowired
    private LivraisonService livraisonService;

    @Autowired
    private VenteService venteService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private StatutLivraisonRepository statutLivraisonRepository;

    @Autowired
    private DetailVenteRepository detailVenteRepository;

    @GetMapping
    public String liste(
            @RequestParam(value = "dateDebut", required = false) String dateDebutStr,
            @RequestParam(value = "dateFin", required = false) String dateFinStr,
            @RequestParam(value = "nomClient", required = false) String nomClient,
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size, 
            Model model) {

        LocalDate dateDebut = (dateDebutStr != null && !dateDebutStr.isBlank()) ? LocalDate.parse(dateDebutStr) : null;
        LocalDate dateFin   = (dateFinStr   != null && !dateFinStr.isBlank()) ? LocalDate.parse(dateFinStr)   : null;
        boolean filtreActif = dateDebut != null || dateFin != null || (nomClient != null && !nomClient.isBlank());
        List<Livraison> livraisons;
        if (nomClient != null && !nomClient.isBlank()) {
            livraisons = livraisonService.filtrerLivraisonsParClient(nomClient, dateDebut, dateFin);
        } else if (filtreActif) {
            livraisons = livraisonService.filtrerLivraisons(dateDebut, dateFin);
        } else {
            livraisons = livraisonService.listeLivraison();
        }

        Page<Livraison> livraisonsPage = PaginationUtils.paginerListe(livraisons, page, size);
        StringBuilder url = new StringBuilder("/admin/livraisons?");
        if (nomClient != null && !nomClient.isBlank()) url.append("nomClient=").append(nomClient).append("&");
        if (dateDebutStr != null && !dateDebutStr.isBlank()) url.append("dateDebut=").append(dateDebutStr).append("&");
        if (dateFinStr != null && !dateFinStr.isBlank()) url.append("dateFin=").append(dateFinStr).append("&");
        url.append("size=").append(size).append("&");
        
        String urlFinale = url.toString();
        if (urlFinale.endsWith("&") || urlFinale.endsWith("?")) {
            urlFinale = urlFinale.substring(0, urlFinale.length() - 1);
        }
        

        LocalDate today = LocalDate.now();
        model.addAttribute("livraisons", livraisonsPage.getContent());
        model.addAttribute("currentPage", livraisonsPage.getNumber());
        model.addAttribute("totalPages", livraisonsPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("baseUrl", urlFinale);

        model.addAttribute("dateDebutSelectionnee", dateDebutStr);
        model.addAttribute("dateFinSelectionnee", dateFinStr);
        model.addAttribute("nomClientSelectionne", nomClient);
        model.addAttribute("pageTitle", "Livraisons");
        model.addAttribute("today", today.toString());
        model.addAttribute("todayMinus30", today.minusDays(30).toString());
        return "livraisons/liste";
    }

    @GetMapping("/creation")
    public String creation(Model model) { 
        model.addAttribute("livraison", new Livraison());
        model.addAttribute("ventesNonLivrees", livraisonService.obtenirVentesNonLivrees());
        model.addAttribute("statutsLivraison", statutLivraisonRepository.findAll());
        model.addAttribute("pageTitle", "Nouvelle livraison");
        return "livraisons/formulaireCreation";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "clientNom", required = false) String clientNom,
                       @RequestParam(value = "venteId", required = false) String venteIdStr,
                       @RequestParam(value = "dateLivraison", required = false) String dateLivraisonStr,
                       @RequestParam("adresseLivraison") String adresseLivraison,
                       @RequestParam(value = "fraisLivraison", required = false) String fraisLivraisonStr,
                       @RequestParam(value = "statutCode", required = false) String statutCode,
                       RedirectAttributes ra) {
        try {
            LocalDate dateLivraison = LocalDate.now();
            if (dateLivraisonStr != null && !dateLivraisonStr.isBlank()) {
                dateLivraison = LocalDate.parse(dateLivraisonStr);
            }

            BigDecimal fraisLivraison = BigDecimal.ZERO;
            if (fraisLivraisonStr != null && !fraisLivraisonStr.isBlank()) {
                fraisLivraison = new BigDecimal(fraisLivraisonStr);
            }

            if (clientNom == null || clientNom.isBlank()) {
                throw new RuntimeException("Veuillez saisir le nom du client.");
            }
            
            if (venteIdStr == null || venteIdStr.isBlank()) {
                throw new RuntimeException("Veuillez sélectionner une vente.");
            }

            Integer venteId = Integer.parseInt(venteIdStr);

            Client client = clientService.trouverParNomOuCreer(clientNom.trim());
            if (client == null) {
                throw new RuntimeException("Impossible de créer ou retrouver le client : " + clientNom);
            }

            Vente vente = venteService.trouverVenteParId(Math.toIntExact(venteId));
            if (vente == null) {
                throw new RuntimeException("Vente introuvable avec l'ID : " + venteId);
            }

            livraisonService.creerLivraison(vente, client, dateLivraison, adresseLivraison, fraisLivraison, statutCode);
            ra.addFlashAttribute("success", "Livraison créée avec succès.");
        } catch (DateTimeParseException e) {
            ra.addFlashAttribute("error", "Date de livraison invalide.");
            return "redirect:/admin/livraisons/creation";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/livraisons/creation";
        }

        return "redirect:/admin/livraisons";
    }

    @PostMapping("/changerStatut")
    public String changerStatut(@RequestParam("id") int id,
                                @RequestParam("statutCode") String statutCode,
                                RedirectAttributes ra) {
        try {
            livraisonService.changerStatutLivraison(id, statutCode);
            ra.addFlashAttribute("success", "Statut de livraison mis à jour.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/livraisons";
    }

    @GetMapping("/exportPdf")
    public String exportPdf(@RequestParam("id") int id, RedirectAttributes ra) {
        ra.addFlashAttribute("error", "Export PDF individuel non disponible pour le moment.");
        return "redirect:/admin/livraisons";
    }

    @PostMapping("/supprimer")
    public String supprimer(@RequestParam("id") int id, RedirectAttributes ra) {
        try {
            livraisonService.supprimerLivraison(id);
            ra.addFlashAttribute("success", "Livraison supprimée avec succès.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/livraisons";
    }
}
