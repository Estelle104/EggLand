package com.app.eggland.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.model.Employe;
import com.app.eggland.model.PaiementSalaire;
import com.app.eggland.model.VersementSalaire;
import com.app.eggland.service.EmployeService;
import com.app.eggland.service.PaiementSalaireService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final EmployeService employeService;
    private final PaiementSalaireService paiementSalaireService;

    // ---------- Liste ----------

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("employes", employeService.listerTous());
        return "employes/liste";
    }

    // ---------- Création ----------

    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("employe", new Employe());
        return "employes/form";
    }

    @PostMapping("/nouveau")
    public String creer(@ModelAttribute Employe employe) {
        employeService.creer(employe);
        return "redirect:/employes";
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
                            @RequestParam BigDecimal salaire) {
        employeService.modifier(id, nom, prenom, tel, salaire);
        return "redirect:/employes";
    }

    // ---------- Suppression ----------

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Integer id) {
        employeService.supprimer(id);
        return "redirect:/employes";
    }

    // ---------- Historique des versements ----------

    @GetMapping("/historique")
    public String historique(@RequestParam(required = false) String mois,
                              @RequestParam(required = false) String statut,
                              Model model) {
        List<PaiementSalaire> paiements = (mois != null && !mois.isBlank())
                ? paiementSalaireService.listerParMois(LocalDate.parse(mois + "-01"))
                : paiementSalaireService.listerHistorique();

        if ("paye".equals(statut)) {
            paiements = paiements.stream().filter(PaiementSalaire::getPaye).toList();
        } else if ("attente".equals(statut)) {
            paiements = paiements.stream().filter(p -> !Boolean.TRUE.equals(p.getPaye())).toList();
        }

        // Pour chaque PaiementSalaire (employé + mois), on attache le détail de ses versements.
        List<HistoriqueLigne> lignes = paiements.stream()
                .map(p -> new HistoriqueLigne(p, paiementSalaireService.listerVersements(p)))
                .toList();

        model.addAttribute("lignes", lignes);
        model.addAttribute("listeMois", genererListeMois());
        model.addAttribute("moisSelectionne", mois);
        model.addAttribute("statutSelectionne", statut);
        return "employes/historique";
    }

    // ---------- Récap mensuel ----------

    @GetMapping("/recap")
    public String recap(@RequestParam(required = false) String mois,
                         @RequestParam(required = false) String statut,
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

        model.addAttribute("recap", recap);
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
                          @RequestParam String date) {
        LocalDate moisDate = LocalDate.parse(mois + "-01");
        LocalDate dateVersement = LocalDate.parse(date);
        paiementSalaireService.verser(id, moisDate, montant, dateVersement);
        return "redirect:/employes/recap?mois=" + mois;
    }

    // ---------- Utilitaire ----------

    /** Génère les 12 derniers mois (valeur "yyyy-MM" + libellé "Juin 2026") pour les filtres. */
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