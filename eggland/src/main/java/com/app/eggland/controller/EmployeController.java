package com.app.eggland.controller;

import com.app.eggland.model.Employe;
import com.app.eggland.model.PaiementSalaire;
import com.app.eggland.model.VersementSalaire;
import com.app.eggland.service.EmployeService;
import com.app.eggland.service.PaginationUtils;
import com.app.eggland.service.PaiementSalaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final EmployeService employeService;
    private final PaiementSalaireService paiementSalaireService;

    // ---------- Liste ----------

    @GetMapping
    public String liste(
        @RequestParam(defaultValue="1")int page,
        @RequestParam(defaultValue="5") int size,
        Model model) {
        List<Employe> employes = employeService.listerTous();
        Page<Employe> employesPage = PaginationUtils.paginerListe(employes, page, size);
        model.addAttribute("employes", employesPage.getContent());
        model.addAttribute("currentPage",employesPage.getNumber());
        model.addAttribute("totalPages", employesPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("pageTitle", "Liste des employés");
        return "employes/liste";
    }

    // ---------- Création ----------

    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("employe", new Employe());
        return "employes/form";
    }

    @PostMapping("/nouveau")
    public String creer(@ModelAttribute Employe employe, RedirectAttributes redirectAttributes) {
        try {
            employeService.creer(employe);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/admin/employes/nouveau";
        }
        return "redirect:/admin/employes";
    }

    // ---------- Modification ----------

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Integer id, Model model) {
        model.addAttribute("employe", employeService.trouverParId(id));
        return "employes/form";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Integer id,
                            @RequestParam String nom,
                            @RequestParam String prenom,
                            @RequestParam(required = false) String tel,
                            @RequestParam BigDecimal salaire,
                            @RequestParam String dateEmbauche,
                            RedirectAttributes redirectAttributes) {
        try {
            employeService.modifier(id, nom, prenom, tel, salaire, LocalDate.parse(dateEmbauche));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/employes";
    }

    // ---------- Suppression ----------

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Integer id) {
        employeService.supprimer(id);
        return "redirect:/admin/employes";
    }



    
    // ---------- Historique des versements ----------

    @GetMapping("/historique")
    public String historique(@RequestParam(required = false) String mois,
                              @RequestParam(required = false) String statut,
                              @RequestParam(defaultValue="1")int page,
                              @RequestParam(defaultValue="5") int size,
                              Model model) {
        List<PaiementSalaire> paiements = (mois != null && !mois.isBlank())
                ? paiementSalaireService.listerParMois(LocalDate.parse(mois + "-01"))
                : paiementSalaireService.listerHistorique();

        if ("paye".equals(statut)) {
            paiements = paiements.stream().filter(PaiementSalaire::getPaye).toList();
        } else if ("attente".equals(statut)) {
            paiements = paiements.stream().filter(p -> !Boolean.TRUE.equals(p.getPaye())).toList();
        }
        Page<PaiementSalaire> paiementsPage = PaginationUtils.paginerListe(paiements, page, size);

        // Pour chaque PaiementSalaire (employé + mois), on attache le détail de ses versements.
        List<HistoriqueLigne> lignes = paiements.stream()
                .map(p -> new HistoriqueLigne(p, paiementSalaireService.listerVersements(p)))
                .toList();

            StringBuilder urlPagination = new StringBuilder("/admin/employes/historique?");
            if (mois != null && !mois.isBlank()) {
                urlPagination.append("mois=").append(mois).append("&");
            }
            if (statut != null && !statut.isBlank()) {
                urlPagination.append("statut=").append(statut).append("&");
            }
            
            String urlFinale = urlPagination.toString();
            if (urlFinale.endsWith("&") || urlFinale.endsWith("?")) {
                urlFinale = urlFinale.substring(0, urlFinale.length() - 1);
            }

        Page<HistoriqueLigne> lignesPage = PaginationUtils.paginerListe(lignes, page, size);

        model.addAttribute("lignesPage", lignesPage.getContent());
        model.addAttribute("currentPage", lignesPage.getNumber());
        model.addAttribute("totalPages", lignesPage.getTotalPages());
        model.addAttribute("size", size);
 
        model.addAttribute("currentPage", paiementsPage.getNumber()); 
        model.addAttribute("totalPages", paiementsPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("baseUrl", urlFinale);

        model.addAttribute("listeMois", genererListeMois());
        model.addAttribute("moisSelectionne", mois);
        model.addAttribute("statutSelectionne", statut);
        return "employes/historique";
    }






    // ---------- Récap mensuel ----------

    @GetMapping("/recap")
    public String recap(@RequestParam(required = false) String mois,
                        @RequestParam(required = false) String statut,
                        @RequestParam(defaultValue="1")int page,
                        @RequestParam(defaultValue="5") int size,
                        Model model) {
        LocalDate moisDate = (mois != null && !mois.isBlank())
                ? LocalDate.parse(mois + "-01")
                : LocalDate.now().withDayOfMonth(1);

        List<PaiementSalaireService.RecapLigne> recap = paiementSalaireService.recapMois(moisDate);

        if ("paye".equals(statut)) {
            recap = recap.stream().filter(PaiementSalaireService.RecapLigne::paye).toList();
        } else if ("attente".equals(statut)) {
            recap = recap.stream().filter(r -> !r.paye()).toList();
        }

        long nbPayes = recap.stream().filter(PaiementSalaireService.RecapLigne::paye).count();
        long nbEnAttente = recap.size() - nbPayes;

        Page<PaiementSalaireService.RecapLigne> recapPage = PaginationUtils.paginerListe(recap, page, size);
        StringBuilder urlPagination = new StringBuilder("/admin/employes/recap?");
        if (mois != null && !mois.isBlank()) {
            urlPagination.append("mois=").append(mois).append("&");
        }
        if (statut != null && !statut.isBlank()) {
            urlPagination.append("statut=").append(statut).append("&");
        }
        
        String urlFinale = urlPagination.toString();
        if (urlFinale.endsWith("&") || urlFinale.endsWith("?")) {
            urlFinale = urlFinale.substring(0, urlFinale.length() - 1);
        }


        model.addAttribute("recap", recapPage.getContent());
        model.addAttribute("currentPage", recapPage.getNumber());
        model.addAttribute("totalPages", recapPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("baseUrl", urlFinale);


        model.addAttribute("nbPayes", nbPayes);
        model.addAttribute("nbEnAttente", nbEnAttente);
        model.addAttribute("moisLabel", paiementSalaireService.formatMoisLabel(moisDate));
        model.addAttribute("listeMois", genererListeMois());
        model.addAttribute("moisSelectionne", YearMonth.from(moisDate).toString());
        model.addAttribute("statutSelectionne", statut);
        return "employes/recap";
    }



    @PostMapping("/{id}/verser")
    public String verser(@PathVariable Integer id,
                          @RequestParam String mois,
                          @RequestParam BigDecimal montant,
                          @RequestParam String date,
                          RedirectAttributes redirectAttributes) {
        LocalDate moisDate = LocalDate.parse(mois + "-01");
        LocalDate dateVersement = LocalDate.parse(date);

        try {
            paiementSalaireService.verser(id, moisDate, montant, dateVersement);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/admin/employes/recap?mois=" + mois;
    }

    // ---------- Utilitaire ----------

    /** Génère les mois pour les filtres : 2 mois à venir + le mois actuel + 12 mois passés. */
    private List<MoisOption> genererListeMois() {
        YearMonth courant = YearMonth.now();
        List<MoisOption> mois = new java.util.ArrayList<>();

        for (int i = 2; i >= -12; i--) {
            YearMonth ym = courant.plusMonths(i);
            String nom = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
            String label = nom.substring(0, 1).toUpperCase() + nom.substring(1) + " " + ym.getYear();
            mois.add(new MoisOption(ym.toString(), label));
        }

        return mois;
    }

    public record MoisOption(String value, String label) {
    }

    /** Une ligne d'historique : le résumé PaiementSalaire (employé + mois) et le détail de ses versements. */
    public record HistoriqueLigne(PaiementSalaire paiementSalaire, List<VersementSalaire> versements) {
    }
}
