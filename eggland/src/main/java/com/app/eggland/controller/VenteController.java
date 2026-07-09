package com.app.eggland.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.app.eggland.model.Vente;
import com.app.eggland.service.ClientService;
import com.app.eggland.service.DetailVenteService;
import com.app.eggland.service.LotService;
import com.app.eggland.service.PaginationUtils;
import com.app.eggland.service.VenteService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/ventes")
public class VenteController {
    @Autowired
    private VenteService venteService;

    @Autowired
    private DetailVenteService detailVenteService;

    @Autowired
    private LotService lotService;

    @Autowired
    private ClientService clientService;

    @GetMapping({"", "/"})
    public String afficherVentes() {
        return "redirect:/admin/ventes/listevente";
    }

    @GetMapping("/listevente")
    public String listeVente(
            @RequestParam(value = "clientId", required = false) Integer clientId,
            @RequestParam(value = "statutId", required = false) Integer statutId,
            @RequestParam(value = "dateDebut", required = false) String dateDebutStr,
            @RequestParam(value = "dateFin", required = false) String dateFinStr,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        LocalDate dateDebut = (dateDebutStr != null && !dateDebutStr.isBlank())
                ? LocalDate.parse(dateDebutStr) : null;
        LocalDate dateFin = (dateFinStr != null && !dateFinStr.isBlank())
                ? LocalDate.parse(dateFinStr) : null;

        boolean filtreActif = clientId != null || statutId != null
                || dateDebut != null || dateFin != null;

        List<Vente> ventes = filtreActif
                ? venteService.filtrerVentes(clientId, statutId, dateDebut, dateFin)
                : venteService.listeVente();
        Page<Vente> ventesPage =PaginationUtils.paginerListe(ventes, page, size);
        Map<String, String> filtres = new java.util.HashMap<>();
        if (clientId != null) filtres.put("clientId", clientId.toString());
        if (statutId != null) filtres.put("statutId", statutId.toString());
        if (dateDebut != null) filtres.put("dateDebut", dateDebut.toString());
        if (dateFin != null) filtres.put("dateFin", dateFin.toString());

        StringBuilder baseUrlBuilder = new StringBuilder("/admin/ventes/listevente?");
        for (Map.Entry<String, String> entry : filtres.entrySet()) {
            baseUrlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String baseUrl = baseUrlBuilder.toString();
        model.addAttribute("clients", clientService.listeClient());
        model.addAttribute("statuts", venteService.listeStatutVente());
        model.addAttribute("clientIdSelectionne", clientId);
        model.addAttribute("statutIdSelectionne", statutId);
        model.addAttribute("dateDebutSelectionnee", dateDebutStr);
        model.addAttribute("dateFinSelectionnee", dateFinStr);
        model.addAttribute("hideSearch", true);

        model.addAttribute("ventes", ventesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ventesPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("filtres", filtres);
        return "vente/listeVente";
    }

    @GetMapping("/creation")
    public String creationVente(Model model) {
        model.addAttribute("produits", venteService.listeProduitVente());
        model.addAttribute("vente", new Vente());
        model.addAttribute("clients", clientService.listeClient());
        model.addAttribute("lots", lotService.getAllLots());
        model.addAttribute("hideSearch", true);
        return "vente/formulairecreation";
    }

    @PostMapping("/creation")
    public String creerVente(HttpServletRequest request, RedirectAttributes ra) {
        try {
            int clientId = request.getParameter("clientId") != null && !request.getParameter("clientId").isBlank()
                    ? Integer.parseInt(request.getParameter("clientId")) : -1;
            if (clientId == -1) {
                ra.addFlashAttribute("error", "Veuillez sélectionner un client.");
                return "redirect:/admin/ventes/creation";
            }
            Client client = clientService.trouverClientParId(clientId);
            if (client == null) {
                ra.addFlashAttribute("error", "Client introuvable.");
                return "redirect:/admin/ventes/creation";
            }
            // Client client = clientService.trouverParNomOuCreer(clientNom);
            // if (client == null) {
            //     ra.addFlashAttribute("error", "Client introuvable.");
            //     return "redirect:/admin/ventes/creation";
            // }

            String[] produitIdsStr = request.getParameterValues("produitId");
            String[] quantitesStr = request.getParameterValues("quantite");
            String[] prixStr = request.getParameterValues("prixUnitaire");
            String[] lotIdsStr = request.getParameterValues("lotId");
            String[] raceIdsStr = request.getParameterValues("raceId");

            if (produitIdsStr == null || produitIdsStr.length == 0) {
                ra.addFlashAttribute("error", "Ajoutez au moins une ligne de vente.");
                return "redirect:/admin/ventes/creation";
            }

            List<Integer> produitIds = new ArrayList<>();
            List<BigDecimal> quantites = new ArrayList<>();
            List<BigDecimal> prix = new ArrayList<>();
            List<Integer> lotIds = new ArrayList<>();
            List<Integer> raceIds = new ArrayList<>();

            for (int i = 0; i < produitIdsStr.length; i++) {
                if (produitIdsStr[i] == null || produitIdsStr[i].isBlank()) continue;
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

                Integer raceId = null;
                if (raceIdsStr != null && i < raceIdsStr.length
                        && raceIdsStr[i] != null && !raceIdsStr[i].isBlank()) {
                    raceId = Integer.parseInt(raceIdsStr[i]);
                }
                raceIds.add(raceId);
            }

            if (produitIds.isEmpty()) {
                ra.addFlashAttribute("error", "Remplissez au moins une ligne de vente complète.");
                return "redirect:/admin/ventes/creation";
            }

            venteService.enregistrerVente(client.getId(), produitIds, lotIds, raceIds, quantites, prix, client);
            ra.addFlashAttribute("success", "Vente créée avec succès.");

        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/ventes/creation";
        }

        return "redirect:/admin/ventes";
    }

    @PostMapping("/supprimer")
    public String supprimerVente(@RequestParam("id") int id, RedirectAttributes ra) {
        try {
            venteService.supprimerVente(id);
            ra.addFlashAttribute("success", "Vente supprimée.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur suppression : " + e.getMessage());
        }
        return "redirect:/admin/ventes/listevente";
    }

    @PostMapping("/modifier")
    public String modifierVente(@RequestParam("id") int id, Model model, RedirectAttributes ra) {
        Vente vente = venteService.trouverVenteParId(id);
        if (vente == null) {
            ra.addFlashAttribute("error", "Vente introuvable.");
            return "redirect:/admin/ventes/listevente";
        }
        model.addAttribute("vente", vente);
        model.addAttribute("details", venteService.listeDetailVente(id));
        model.addAttribute("clients", clientService.listeClient());
        model.addAttribute("produits", venteService.listeProduitVente());
        model.addAttribute("lots", lotService.getAllLots());
        return "vente/formulaireModification";
    }

    @PostMapping("/detail")
    public String detailVente(@RequestParam("id") int id,
                              @RequestParam("clientId") int clientId,
                              Model model, RedirectAttributes ra) {
        Vente vente = venteService.trouverVenteParId(id);
        if (vente == null) {
            ra.addFlashAttribute("error", "Vente introuvable.");
            return "redirect:/admin/ventes/listevente";
        }
        model.addAttribute("vente", vente);
        model.addAttribute("details", venteService.listeDetailVente(id));
        return "vente/detailVente";
    }

    @PostMapping("/enregistrerModification")
    public String enregistrerModificationVente(HttpServletRequest request, RedirectAttributes ra) {
        try {
            String venteIdStr = request.getParameter("id");
            if (venteIdStr == null || venteIdStr.isBlank()) {
                ra.addFlashAttribute("error", "Identifiant de vente manquant.");
                return "redirect:/admin/ventes/listevente";
            }
            int venteId = Integer.parseInt(venteIdStr);

            String clientIdStr = request.getParameter("clientId");
            if (clientIdStr == null || clientIdStr.isBlank()) {
                ra.addFlashAttribute("error", "Veuillez sélectionner un client.");
                return "redirect:/admin/ventes/listevente";
            }
            Client client = clientService.trouverClientParId(Integer.parseInt(clientIdStr));
            if (client == null) {
                ra.addFlashAttribute("error", "Client introuvable.");
                return "redirect:/admin/ventes/listevente";
            }

            String[] produitIdsStr = request.getParameterValues("produitId");
            String[] quantitesStr = request.getParameterValues("quantite");
            String[] prixStr = request.getParameterValues("prixUnitaire");
            String[] lotIdsStr = request.getParameterValues("lotId");
            String[] raceIdsStr = request.getParameterValues("raceId");

            if (produitIdsStr == null || produitIdsStr.length == 0) {
                ra.addFlashAttribute("error", "La vente doit contenir au moins une ligne.");
                return "redirect:/admin/ventes/listevente";
            }

            List<Integer> produitIds = new ArrayList<>();
            List<BigDecimal> quantites = new ArrayList<>();
            List<BigDecimal> prix = new ArrayList<>();
            List<Integer> lotIds = new ArrayList<>();
            List<Integer> raceIds = new ArrayList<>();

            for (int i = 0; i < produitIdsStr.length; i++) {
                if (produitIdsStr[i] == null || produitIdsStr[i].isBlank()) continue;
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

                Integer raceId = null;
                if (raceIdsStr != null && i < raceIdsStr.length
                        && raceIdsStr[i] != null && !raceIdsStr[i].isBlank()) {
                    raceId = Integer.parseInt(raceIdsStr[i]);
                }
                raceIds.add(raceId);
            }

            if (produitIds.isEmpty()) {
                ra.addFlashAttribute("error", "Veuillez remplir correctement au moins une ligne complète.");
                return "redirect:/admin/ventes/listevente";
            }

            venteService.enregistrerModificationVente(venteId, produitIds, lotIds, raceIds, quantites, prix, client);
            ra.addFlashAttribute("success", "Vente modifiée avec succès !");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            return "redirect:/admin/ventes/listevente";
        }

        return "redirect:/admin/ventes/listevente";
    }

}
