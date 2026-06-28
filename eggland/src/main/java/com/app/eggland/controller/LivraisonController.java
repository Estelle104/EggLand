package com.app.eggland.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.eggland.model.Client;
import com.app.eggland.model.Livraison;
import com.app.eggland.model.ProduitVente;
import com.app.eggland.model.Vente;
import com.app.eggland.repository.StatutLivraisonRepository;
import com.app.eggland.service.ClientService;
import com.app.eggland.service.LivraisonService;
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

    @GetMapping
    public String liste(
            @RequestParam(value = "dateDebut", required = false) String dateDebutStr,
            @RequestParam(value = "dateFin", required = false) String dateFinStr,
            Model model) {

        LocalDate dateDebut = (dateDebutStr != null && !dateDebutStr.isBlank())
                              ? LocalDate.parse(dateDebutStr) : null;
        LocalDate dateFin   = (dateFinStr   != null && !dateFinStr.isBlank())
                              ? LocalDate.parse(dateFinStr)   : null;

        boolean filtreActif = dateDebut != null || dateFin != null;

        List<Livraison> livraisons = filtreActif
            ? livraisonService.filtrerLivraisons(dateDebut, dateFin)
            : livraisonService.listeLivraison();

        LocalDate today = LocalDate.now();
        model.addAttribute("livraisons", livraisons);
        model.addAttribute("dateDebutSelectionnee", dateDebutStr);
        model.addAttribute("dateFinSelectionnee", dateFinStr);
        model.addAttribute("pageTitle", "Livraisons");
        model.addAttribute("today", today.toString());
        model.addAttribute("todayMinus30", today.minusDays(30).toString());
        return "livraisons/liste";
    }

    @GetMapping("/creation")
    public String creation(Model model) {
        model.addAttribute("livraison", new Livraison());
        model.addAttribute("produits", venteService.listeProduitVente());
        model.addAttribute("statuts", statutLivraisonRepository.findAll());
        model.addAttribute("pageTitle", "Nouvelle livraison");
        return "livraisons/formulaireCreation";
    }

    @PostMapping("/save")
    public String save(@RequestParam("produitCode") String produitCode,
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

            if (statutCode == null || statutCode.isBlank()) {
                statutCode = "en_attente";
            }

            if (produitCode == null || produitCode.isBlank()) {
                throw new RuntimeException("Veuillez sélectionner un type de livraison.");
            }

            ProduitVente produit = venteService.trouverProduitVenteParCode(produitCode);
            if (produit == null) {
                throw new RuntimeException("Produit introuvable : " + produitCode);
            }

            var clients = clientService.listeClient();
            Client client;
            if (clients != null && !clients.isEmpty()) {
                client = clients.get(0);
            } else {
                Client anon = new Client();
                anon.setNom("Client anonyme");
                anon.setEmail("anonyme@eggland.local");
                anon.setAdresse("Créé automatiquement");
                anon.setDateInscription(LocalDate.now());
                client = clientService.registerClient(anon);
            }

            var statutVente = venteService.trouverStatutVenteParCode("paye");
            if (statutVente == null) {
                throw new RuntimeException("Statut de vente 'paye' introuvable. Initialisez la base de données.");
            }

            Vente nouvelleVente = Vente.builder()
                    .client(client)
                    .date(LocalDate.now())
                    .total(BigDecimal.ZERO)
                    .statut(statutVente)
                    .build();
            venteService.saveVente(nouvelleVente);

            livraisonService.creerLivraisonDepuisVente(nouvelleVente.getId(), dateLivraison, adresseLivraison, fraisLivraison, statutCode);
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
