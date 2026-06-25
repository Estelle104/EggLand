package com.app.eggland.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.eggland.model.Lot;
import com.app.eggland.model.OeufProduction;
import com.app.eggland.model.OeufStatut;
import com.app.eggland.model.StatutOeuf;
import com.app.eggland.repository.LotRepository;
import com.app.eggland.repository.OeufProductionRepository;
import com.app.eggland.repository.StatutOeufRepository;

@Service
public class OeufProductionService {

    @Autowired
    private OeufProductionRepository oeufProductionRepository;

    @Autowired
    private StatutOeufRepository statutOeufRepository;

    @Autowired
    private LotRepository lotRepository;

    @Transactional
    public OeufProduction addOeufProduction(OeufProduction production) {
        verifierProduction(production);

        Lot lot = lotRepository.findById(production.getLot().getId())
                .orElseThrow(() -> new RuntimeException("Lot introuvable"));

        if (lot.getStatut() == null || !"actif".equalsIgnoreCase(lot.getStatut().getCode())) {
            throw new RuntimeException("Seul un lot actif peut produire des œufs");
        }

        if (lot.getDateArrivee() != null && production.getDate().isBefore(lot.getDateArrivee())) {
            throw new RuntimeException("La collecte ne peut pas précéder l'arrivée du lot");
        }

        boolean existeDeja;
        if (production.getId() == null) {
            existeDeja = oeufProductionRepository.existsByLotIdAndDate(lot.getId(), production.getDate());
        } else {
            if (!oeufProductionRepository.existsById(production.getId())) {
                throw new RuntimeException("Production introuvable");
            }
            existeDeja = oeufProductionRepository.existsByLotIdAndDateAndIdNot(
                    lot.getId(), production.getDate(), production.getId());
        }

        if (existeDeja) {
            throw new RuntimeException("Une production existe déjà pour ce lot à cette date");
        }

        production.setLot(lot);
        production.setOeufStatuts(preparerStatuts(production));
        return oeufProductionRepository.save(production);
    }

    public List<OeufProduction> getAllOeufsWithDetails() {
        return oeufProductionRepository.findAllByOrderByDateDescIdDesc();
    }

    private void verifierProduction(OeufProduction production) {
        if (production == null) {
            throw new RuntimeException("La production est obligatoire");
        }
        if (production.getLot() == null || production.getLot().getId() == null) {
            throw new RuntimeException("Le lot est obligatoire");
        }
        if (production.getDate() == null) {
            throw new RuntimeException("La date est obligatoire");
        }
        if (production.getDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Une collecte ne peut pas être enregistrée dans le futur");
        }
        if (production.getQuantite() == null || production.getQuantite() <= 0) {
            throw new RuntimeException("La quantité doit être strictement positive");
        }
    }

    private List<OeufStatut> preparerStatuts(OeufProduction production) {
        List<OeufStatut> resultat = new ArrayList<>();
        Set<Integer> statutsUtilises = new HashSet<>();
        int totalAnomalies = 0;

        if (production.getOeufStatuts() != null) {
            for (OeufStatut ligne : production.getOeufStatuts()) {
                if (ligneVide(ligne)) {
                    continue;
                }

                if (ligne.getStatut() == null || ligne.getStatut().getId() == null) {
                    throw new RuntimeException("Chaque anomalie doit avoir un statut");
                }
                if (ligne.getQuantite() == null || ligne.getQuantite() <= 0) {
                    throw new RuntimeException("La quantité d'une anomalie doit être positive");
                }

                StatutOeuf statut = statutOeufRepository.findById(ligne.getStatut().getId())
                        .orElseThrow(() -> new RuntimeException("Statut d'œuf introuvable"));

                if ("valide".equalsIgnoreCase(statut.getCode())) {
                    throw new RuntimeException("La quantité valide est calculée automatiquement");
                }
                if ("vendu".equalsIgnoreCase(statut.getCode())) {
                    throw new RuntimeException("Le statut vendu ne peut pas être saisi pendant une collecte");
                }
                if (!statutsUtilises.add(statut.getId())) {
                    throw new RuntimeException("Un statut ne peut apparaître qu'une fois par collecte");
                }

                totalAnomalies += ligne.getQuantite();
                if (totalAnomalies > production.getQuantite()) {
                    throw new RuntimeException("Les anomalies dépassent la quantité totale produite");
                }

                OeufStatut anomalie = new OeufStatut();
                anomalie.setProduction(production);
                anomalie.setStatut(statut);
                anomalie.setQuantite(ligne.getQuantite());
                resultat.add(anomalie);
            }
        }

        int quantiteValide = production.getQuantite() - totalAnomalies;
        if (quantiteValide > 0) {
            StatutOeuf statutValide = statutOeufRepository.findByCode("valide")
                    .orElseThrow(() -> new RuntimeException("Statut valide introuvable"));

            OeufStatut valide = new OeufStatut();
            valide.setProduction(production);
            valide.setStatut(statutValide);
            valide.setQuantite(quantiteValide);
            resultat.add(valide);
        }

        return resultat;
    }

    private boolean ligneVide(OeufStatut ligne) {
        if (ligne == null) {
            return true;
        }
        boolean statutVide = ligne.getStatut() == null || ligne.getStatut().getId() == null;
        boolean quantiteVide = ligne.getQuantite() == null || ligne.getQuantite() == 0;
        return statutVide && quantiteVide;
    }

    public List<Map<String, Object>> getTauxPonteParLot() {
        List<OeufProduction> productions = getAllOeufsWithDetails();
        List<Map<String, Object>> result = new ArrayList<>();
        for (OeufProduction production : productions) {
            result.add(buildTauxPonteParLot(production));
        }
        return result;
    }

    public Map<String, Object> getProductionDes14DerniersJours() {
        LocalDate dateFin = LocalDate.now();
        LocalDate dateDebut = dateFin.minusDays(13);
        Map<LocalDate, Integer> quantitesParDate = new HashMap<>();

        for (Object[] ligne : oeufProductionRepository.sumQuantiteParDate(dateDebut, dateFin)) {
            LocalDate date = (LocalDate) ligne[0];
            int quantite = ((Number) ligne[1]).intValue();
            quantitesParDate.put(date, quantite);
        }

        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd/MM");
        List<String> labels = new ArrayList<>();
        List<Integer> quantites = new ArrayList<>();

        for (LocalDate date = dateDebut; !date.isAfter(dateFin); date = date.plusDays(1)) {
            labels.add(date.format(formatDate));
            quantites.add(quantitesParDate.getOrDefault(date, 0));
        }

        Map<String, Object> donneesGraphique = new LinkedHashMap<>();
        donneesGraphique.put("labels", labels);
        donneesGraphique.put("quantites", quantites);
        return donneesGraphique;
    }

    private Map<String, Object> buildTauxPonteParLot(OeufProduction production) {
        int quantiteValide = production.getOeufStatuts().stream()
                .filter(s -> s.getStatut() != null && "valide".equalsIgnoreCase(s.getStatut().getCode()))
                .mapToInt(OeufStatut::getQuantite)
                .sum();

        Lot lot = production.getLot();
        double rendementMensuel = lot.getRace().getRendementMoyenMois();
        double attenduParJourParPoule = rendementMensuel / 30.0;
        double attenduLotJour = lot.getNombreInitial() * attenduParJourParPoule;
        double taux = attenduLotJour == 0 ? 0 : (quantiteValide / attenduLotJour) * 100;

        Map<String, Object> stat = new HashMap<>();
        stat.put("lotNumero", lot.getId());
        stat.put("raceNom", lot.getRace().getNom());
        stat.put("date", production.getDate());
        stat.put("quantiteValide", quantiteValide);
        stat.put("attendu", attenduLotJour);
        stat.put("taux", taux);
        return stat;
    }
}
