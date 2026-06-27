package com.app.eggland.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.eggland.model.Client;
import com.app.eggland.model.DetailVente;
import com.app.eggland.model.Vente;
import com.app.eggland.model.Lot;
import com.app.eggland.model.ProduitVente;
import com.app.eggland.service.ClientService;
import com.app.eggland.service.DetailVenteService;
import com.app.eggland.service.LotService;
import com.app.eggland.service.VenteService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class VenteController {
    @Autowired
    private VenteService venteService;

    @Autowired
    private DetailVenteService detailVenteService;

    @Autowired
    private LotService lotService;

    @Autowired
    private ClientService clientService;
    
    @GetMapping("/ventes")
    public String afficherVentes() {
        return "redirect:/ventes/listevente";
    }

    @GetMapping("/ventes/listevente")
    public String listeVente(Model model) {
        model.addAttribute("ventes", venteService.listeVente());
        return "vente/listeVente";
    }

    @GetMapping("/ventes/creation")
    public String creationVente(Model model) {
        model.addAttribute("produits", venteService.listeProduitVente());
        model.addAttribute("vente", new Vente());
        model.addAttribute("lots", lotService.getAllLotsActifs());
        model.addAttribute("clients", clientService.listeClient());
        return "vente/formulairecreation";
    }

    
    @PostMapping("/ventes/creation")
    public String creerVente(HttpServletRequest request, RedirectAttributes ra) {
        try {
            // --- Client ---
            String clientIdStr = request.getParameter("clientId");
            if (clientIdStr == null || clientIdStr.isBlank()) {
                ra.addFlashAttribute("error", "Veuillez sélectionner un client.");
                return "redirect:/ventes/creation";
            }
            Client client = clientService.trouverClientParId(Integer.parseInt(clientIdStr));
            if (client == null) {
                ra.addFlashAttribute("error", "Client introuvable.");
                return "redirect:/ventes/creation";
            }

            // --- Lignes : les tableaux sont alignés dans l'ordre d'envoi ---
            String[] produitIdsStr = request.getParameterValues("produitId");
            String[] quantitesStr  = request.getParameterValues("quantite");
            String[] prixStr       = request.getParameterValues("prixUnitaire");
            String[] lotIdsStr     = request.getParameterValues("lotId");

            if (produitIdsStr == null || produitIdsStr.length == 0) {
                ra.addFlashAttribute("error", "Ajoutez au moins une ligne de vente.");
                return "redirect:/ventes/creation";
            }

            List<Integer>    produitIds = new ArrayList<>();
            List<BigDecimal> quantites  = new ArrayList<>();
            List<BigDecimal> prix       = new ArrayList<>();
            List<Integer>    lotIds     = new ArrayList<>();

            for (int i = 0; i < produitIdsStr.length; i++) {
                // Ignorer les lignes sans produit sélectionné
                if (produitIdsStr[i] == null || produitIdsStr[i].isBlank()) continue;
                // Ignorer les lignes sans quantité ou prix
                if (quantitesStr == null || i >= quantitesStr.length
                        || quantitesStr[i] == null || quantitesStr[i].isBlank()) continue;
                if (prixStr == null || i >= prixStr.length
                        || prixStr[i] == null || prixStr[i].isBlank()) continue;

                produitIds.add(Integer.parseInt(produitIdsStr[i]));
                quantites.add(new BigDecimal(quantitesStr[i]));
                prix.add(new BigDecimal(prixStr[i]));

                Integer lotId = null;
                if (lotIdsStr != null && i < lotIdsStr.length
                        && lotIdsStr[i] != null && !lotIdsStr[i].isBlank()) {
                    lotId = Integer.parseInt(lotIdsStr[i]);
                }
                lotIds.add(lotId);
            }

            if (produitIds.isEmpty()) {
                ra.addFlashAttribute("error", "Remplissez au moins une ligne de vente complète.");
                return "redirect:/ventes/creation";
            }

            venteService.enregistrerVente(client.getId(), produitIds, lotIds, quantites, prix, client);
            ra.addFlashAttribute("success", "Vente créée avec succès.");

        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/ventes/creation";
        }

        return "redirect:/ventes";
    }

   
    @PostMapping("/ventes/supprimer")
    public String supprimerVente(@RequestParam("id") int id, RedirectAttributes ra) {
        try {
            venteService.supprimerVente(id);
            ra.addFlashAttribute("success", "Vente supprimée.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur suppression : " + e.getMessage());
        }
        return "redirect:/ventes/listevente";
    }

 
    @PostMapping("/ventes/modifier")
    public String modifierVente(@RequestParam("id") int id, Model model, RedirectAttributes ra) {
        Vente vente = venteService.trouverVenteParId(id);
        if (vente == null) {
            ra.addFlashAttribute("error", "Vente introuvable.");
            return "redirect:/ventes/listevente";
        }
        model.addAttribute("vente", vente);
        model.addAttribute("details", venteService.listeDetailVente(id));
        model.addAttribute("clients", clientService.listeClient());
        model.addAttribute("produits", venteService.listeProduitVente());
        model.addAttribute("lots", lotService.getAllLotsActifs());
        return "vente/formulaireModification";
    }

  
    @PostMapping("/ventes/detail")
    public String detailVente(@RequestParam("id") int id,
                               @RequestParam("clientId") int clientId,
                               Model model, RedirectAttributes ra) {
        Vente vente = venteService.trouverVenteParId(id);
        if (vente == null) {
            ra.addFlashAttribute("error", "Vente introuvable.");
            return "redirect:/ventes/listevente";
        }
        model.addAttribute("vente", vente);
        model.addAttribute("details", venteService.listeDetailVente(id));
        return "vente/detailVente";
    }

    @PostMapping("/ventes/enregistrerModification")
    public String enregistrerModificationVente(HttpServletRequest request, RedirectAttributes ra) {
        try {
            // --- ID de la Vente ---
            String venteIdStr = request.getParameter("id");
            if (venteIdStr == null || venteIdStr.isBlank()) {
                ra.addFlashAttribute("error", "Identifiant de vente manquant.");
                return "redirect:/ventes/listevente";
            }
            int venteId = Integer.parseInt(venteIdStr);

            // --- Client ---
            String clientIdStr = request.getParameter("clientId");
            if (clientIdStr == null || clientIdStr.isBlank()) {
                ra.addFlashAttribute("error", "Veuillez sélectionner un client.");
                return "redirect:/ventes/modifier?id=" + venteId;
            }
            Client client = clientService.trouverClientParId(Integer.parseInt(clientIdStr));
            if (client == null) {
                ra.addFlashAttribute("error", "Client introuvable.");
                return "redirect:/ventes/modifier?id=" + venteId;
            }

            // --- Lignes dynamiques envoyées par le tableau ---
            String[] produitIdsStr = request.getParameterValues("produitId");
            String[] quantitesStr  = request.getParameterValues("quantite");
            String[] prixStr       = request.getParameterValues("prixUnitaire");
            String[] lotIdsStr     = request.getParameterValues("lotId");

            if (produitIdsStr == null || produitIdsStr.length == 0) {
                ra.addFlashAttribute("error", "La vente doit contenir au moins une ligne.");
                return "redirect:/ventes/modifier?id=" + venteId;
            }

            List<Integer>    produitIds = new ArrayList<>();
            List<BigDecimal> quantites  = new ArrayList<>();
            List<BigDecimal> prix       = new ArrayList<>();
            List<Integer>    lotIds     = new ArrayList<>();

            for (int i = 0; i < produitIdsStr.length; i++) {
                if (produitIdsStr[i] == null || produitIdsStr[i].isBlank()) continue;
                if (quantitesStr == null || i >= quantitesStr.length || quantitesStr[i] == null || quantitesStr[i].isBlank()) continue;
                if (prixStr == null || i >= prixStr.length || prixStr[i] == null || prixStr[i].isBlank()) continue;

                produitIds.add(Integer.parseInt(produitIdsStr[i]));
                quantites.add(new BigDecimal(quantitesStr[i]));
                prix.add(new BigDecimal(prixStr[i]));

                Integer lotId = null;
                if (lotIdsStr != null && i < lotIdsStr.length && lotIdsStr[i] != null && !lotIdsStr[i].isBlank()) {
                    lotId = Integer.parseInt(lotIdsStr[i]);
                }
                lotIds.add(lotId);
            }

            if (produitIds.isEmpty()) {
                ra.addFlashAttribute("error", "Veuillez remplir correctement au moins une ligne complète.");
                return "redirect:/ventes/modifier?id=" + venteId;
            }

            // Exécution de la modification avec calcul différentiel
            venteService.enregistrerModificationVente(venteId, produitIds, lotIds, quantites, prix, client);
            ra.addFlashAttribute("success", "Vente modifiée avec succès !");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            return "redirect:/ventes/listevente";
        }

        return "redirect:/ventes/listevente";
    }
}
