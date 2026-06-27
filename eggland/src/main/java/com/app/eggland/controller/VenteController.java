package com.app.eggland.controller;

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
        List<ProduitVente> produits = venteService.listeProduitVente();
        List<Lot> lots = lotService.getAllLots();
        List<Client> clients = clientService.listeClient();
        model.addAttribute("produits", produits);
        model.addAttribute("vente", new com.app.eggland.model.Vente());
        model.addAttribute("lots", lots);
        model.addAttribute("clients", clients);
        return "vente/formulairecreation";
    }

 @PostMapping("/ventes/creation")
    public String creerVente(HttpServletRequest request,
                             RedirectAttributes redirectAttrs) {
        try {
            String clientIdStr   = request.getParameter("clientId");
            String[] produitIds  = request.getParameterValues("produitId");
            String[] quantites   = request.getParameterValues("quantite");
            String[] prixUnits   = request.getParameterValues("prixUnitaire");
            String[] lotIds      = request.getParameterValues("lotId");

            if (clientIdStr == null || clientIdStr.isBlank()) {
                redirectAttrs.addFlashAttribute("error", "Veuillez sélectionner un client.");
                return "redirect:/ventes/creation";
            }

            Client client = clientService.trouverClientParId(Integer.parseInt(clientIdStr));
            if (client == null) {
                redirectAttrs.addFlashAttribute("error", "Client introuvable.");
                return "redirect:/ventes/creation";
            }

            venteService.enregistrerVente(client, produitIds, quantites, prixUnits, lotIds);
            redirectAttrs.addFlashAttribute("success", "Vente créée avec succès.");

        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/ventes/creation";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Erreur inattendue : " + e.getMessage());
            return "redirect:/ventes/creation";
        }

        return "redirect:/ventes";
    }


    @PostMapping("/ventes/supprimer")
    public String supprimerVente(@RequestParam("id") int id,
                                 RedirectAttributes redirectAttrs) {
        try {
            venteService.supprimerVente(id);
            redirectAttrs.addFlashAttribute("success", "Vente supprimée.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Impossible de supprimer : " + e.getMessage());
        }
        return "redirect:/ventes/listevente";
    }

    @GetMapping("/ventes/modifier")
    public String formulaireModification(@RequestParam("id") int id, Model model) {
        Vente vente = venteService.trouverVenteParId(id);
        if (vente == null) {
            return "redirect:/ventes/listevente";
        }
        List<DetailVente> details = venteService.listeDetailVente(id);
        List<Client> clients = clientService.listeClient();
        List<ProduitVente> produits = venteService.listeProduitVente();
        List<Lot> lots = lotService.getAllLotsActifs();

        model.addAttribute("vente", vente);
        model.addAttribute("details", details);
        model.addAttribute("clients", clients);
        model.addAttribute("produits", produits);
        model.addAttribute("lots", lots);
        return "vente/formulaireModification";
    }

    @PostMapping("/ventes/modifier")
    public String modifierVente(HttpServletRequest request,
                                RedirectAttributes redirectAttrs) {
        try {
            String venteIdStr  = request.getParameter("venteId");
            String clientIdStr = request.getParameter("clientId");

            if (venteIdStr == null || clientIdStr == null) {
                redirectAttrs.addFlashAttribute("error", "Données manquantes.");
                return "redirect:/ventes/listevente";
            }

            int venteId  = Integer.parseInt(venteIdStr);
            Client client = clientService.trouverClientParId(Integer.parseInt(clientIdStr));

            Vente vente = venteService.trouverVenteParId(venteId);
            if (vente == null) {
                redirectAttrs.addFlashAttribute("error", "Vente introuvable.");
                return "redirect:/ventes/listevente";
            }

            // Supprimer les anciens détails et recréer
            List<DetailVente> anciensDetails = venteService.listeDetailVente(venteId);
            for (DetailVente d : anciensDetails) {
                detailVenteService.supprimerDetailVente(d.getId());
            }

            String[] produitIds = request.getParameterValues("produitId");
            String[] quantites  = request.getParameterValues("quantite");
            String[] prixUnits  = request.getParameterValues("prixUnitaire");
            String[] lotIds     = request.getParameterValues("lotId");

            venteService.enregistrerVente(client, produitIds, quantites, prixUnits, lotIds);
            redirectAttrs.addFlashAttribute("success", "Vente modifiée avec succès.");

        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ventes/listevente";
    }


    @PostMapping("/ventes/detail")
    public String detailVente(@RequestParam("id") int id,
                              @RequestParam(value = "clientId", required = false) Integer clientId,
                              Model model) {
        Vente vente = venteService.trouverVenteParId(id);
        if (vente == null) {
            return "redirect:/ventes/listevente";
        }
        List<DetailVente> details = venteService.listeDetailVente(id);
        model.addAttribute("vente", vente);
        model.addAttribute("details", details);
        return "vente/detailVente";
    }
}
