package com.app.eggland.controller;

import com.app.eggland.model.Lot;
import com.app.eggland.model.Mort;
import com.app.eggland.model.Race;
import com.app.eggland.repository.RaceRepository;
import com.app.eggland.service.LotService;
import com.app.eggland.service.MortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/morts")
public class MortController {
    @Autowired
    private MortService mortService;

    @Autowired
    private LotService lotService;

    @Autowired
    private RaceRepository raceRepository;

    @GetMapping("/liste")
    public String liste(Model model) {
        List<Mort> morts = mortService.findAll();

        List<Lot> lots = lotService.findAll();

        // Model pour l'affichage de nombre de poules mort par lot, clee = Lot Id,
        // values nombres de vivant Lot
        Map<Integer, Integer> currentCounts = new HashMap<>();

        // total des morts
        int totalMorts = mortService.getTotalMort();

        for (Lot lot : lots) {
            // complete la map
            currentCounts.put(lot.getId(), mortService.getNombreMort(lot));
        }

        model.addAttribute("morts", morts);
        model.addAttribute("lots", lots);
        model.addAttribute("currentCounts", currentCounts);
        model.addAttribute("totalMorts", totalMorts);
        model.addAttribute("pageTitle", "Nombre de morts par lot");

        return "morts/liste";
    }

    @GetMapping("/insertion")
    public String insertion(Model model) {

        model.addAttribute("mort", new Mort());

        List<Lot> lots = mortService.findAllLotTemporary();

        model.addAttribute("lots", lots);

        List<Race> races = raceRepository.findAll();

        model.addAttribute("races", races);

        model.addAttribute("pageTitle", "Insertion de mort par lot");

        return "morts/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Mort mort) {

        if (mort.getRace() == null) {
            throw new RuntimeException("La race est obligatoire");
        }

        mortService.save(mort);

        return "redirect:/morts/liste";
    }

    @GetMapping("/historique")
    public String historique(Model model) {
        List<Mort> morts = mortService.findAll();
        morts.sort(Comparator.comparing(Mort::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

        int totalMorts = morts.stream()
                .mapToInt(m -> m.getNombre() == null ? 0 : m.getNombre())
                .sum();

        int distinctDays = (int) morts.stream()
                .map(Mort::getDate)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        double averageMortsPerDay = distinctDays > 0 ? totalMorts / (double) distinctDays : 0;

        Map<Integer, Integer> currentCounts = new HashMap<>();
        for (Mort mort : morts) {
            currentCounts.put(mort.getId(), mortService.getNombreActuel(mort.getLot()));
        }

        Map<String, Integer> mortalityByDay = new LinkedHashMap<>();
        for (Mort mort : morts) {
            String label = mort.getDate() != null ? mort.getDate().toString() : "";
            if (label.isEmpty()) {
                continue;
            }
            mortalityByDay.merge(label, mort.getNombre() == null ? 0 : mort.getNombre(), Integer::sum);
        }

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        List<String> monthOptions = morts.stream()
                .map(Mort::getDate)
                .filter(Objects::nonNull)
                .map(date -> date.format(monthFormatter))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("morts", morts);
        model.addAttribute("currentCounts", currentCounts);
        model.addAttribute("lots", mortService.findAllLotTemporary());
        model.addAttribute("pageTitle", "Historique des mortalités");
        model.addAttribute("totalMorts", totalMorts);
        model.addAttribute("averageMortsPerDay", averageMortsPerDay);
        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("chartLabels", new ArrayList<>(mortalityByDay.keySet()));
        model.addAttribute("chartData", new ArrayList<>(mortalityByDay.values()));

        return "morts/historique";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {

        Mort mort = mortService.findById(id)
                .orElseThrow(() -> new RuntimeException("Mort non trouvée"));

        model.addAttribute("mort", mort);

        model.addAttribute("lots",
                mortService.findAllLotTemporary());

        model.addAttribute("races",
                raceRepository.findAll());

        model.addAttribute("pageTitle",
                "Modifier la mort");

        return "morts/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id,
            @RequestParam(required = false) String redirect,
            RedirectAttributes ra) {
        try {
            mortService.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "Impossible de supprimer : cette mort est liée à d'autres données.");
        }
        if ("historique".equalsIgnoreCase(redirect)) {
            return "redirect:/morts/historique";
        }
        return "redirect:/morts/liste";
    }
}
