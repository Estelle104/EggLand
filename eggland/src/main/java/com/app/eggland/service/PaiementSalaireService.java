package com.app.eggland.service;

import com.app.eggland.model.Employe;
import com.app.eggland.model.PaiementSalaire;
import com.app.eggland.model.VersementSalaire;
import com.app.eggland.repository.PaiementSalaireRepository;
import com.app.eggland.repository.VersementSalaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PaiementSalaireService {

    private final PaiementSalaireRepository paiementSalaireRepository;
    private final VersementSalaireRepository versementSalaireRepository;
    private final EmployeService employeService;
    private final MvtArgentService mvtArgentService;

    public List<PaiementSalaire> listerHistorique() {
        return paiementSalaireRepository.findAllByOrderByMoisDescDatePaiementDesc();
    }

    public List<PaiementSalaire> listerParMois(LocalDate mois) {
        return paiementSalaireRepository.findByMoisOrderByEmployeNomAsc(mois.withDayOfMonth(1));
    }

    /** Tous les versements (toutes périodes), du plus récent au plus ancien. */
    public List<VersementSalaire> listerTousLesVersements() {
        return versementSalaireRepository.findAllByOrderByDateDesc();
    }

    /** Détail des versements pour un PaiementSalaire donné (un mois/employé précis). */
    public List<VersementSalaire> listerVersements(PaiementSalaire paiementSalaire) {
        return versementSalaireRepository.findByPaiementSalaireOrderByDateAsc(paiementSalaire);
    }

    /**
     * Enregistre un versement (paiement partiel ou total) du salaire d'un employé pour le
     * mois donné. Met à jour le cumulé dans PaiementSalaire, garde le détail dans
     * VersementSalaire, et crée un MvtArgent "sortie" / catégorie "salaire" à la date choisie.
     */
    public PaiementSalaire verser(Integer employeId, LocalDate mois, BigDecimal montantVerse, LocalDate dateVersement) {
        Employe employe = employeService.trouverParId(employeId);
        LocalDate premierJourMois = mois.withDayOfMonth(1);

        verifierPosterieurEmbauche(employe, premierJourMois, dateVersement);

        PaiementSalaire paiement = paiementSalaireRepository
                .findByEmployeAndMois(employe, premierJourMois)
                .orElse(PaiementSalaire.builder()
                        .employe(employe)
                        .mois(premierJourMois)
                        .montant(BigDecimal.ZERO)
                        .paye(false)
                        .build());

        verifierNeDepassePasSalaireDu(employe, paiement.getMontant(), montantVerse);

        BigDecimal nouveauCumul = paiement.getMontant().add(montantVerse);
        paiement.setMontant(nouveauCumul);
        paiement.setDatePaiement(dateVersement);
        paiement.setPaye(nouveauCumul.compareTo(employe.getSalaire()) >= 0);

        PaiementSalaire saved = paiementSalaireRepository.save(paiement);

        VersementSalaire versement = VersementSalaire.builder()
                .paiementSalaire(saved)
                .montant(montantVerse)
                .date(dateVersement)
                .build();
        versementSalaireRepository.save(versement);

        mvtArgentService.creerSortie(montantVerse, dateVersement, "salaire");

        return saved;
    }

    /**
     * Récap du mois : pourcentage payé pour chaque employé
     * (0% si aucun versement, 100% si cumul >= salaire dû).
     */
    public List<RecapLigne> recapMois(LocalDate mois) {
        LocalDate premierJourMois = mois.withDayOfMonth(1);
        List<Employe> employes = employeService.listerTous();

        return employes.stream().map(employe -> {
            PaiementSalaire paiement = paiementSalaireRepository
                    .findByEmployeAndMois(employe, premierJourMois)
                    .orElse(null);

            BigDecimal montantVerse = paiement != null ? paiement.getMontant() : BigDecimal.ZERO;
            boolean paye = paiement != null && Boolean.TRUE.equals(paiement.getPaye());
            LocalDate dernierVersement = paiement != null ? paiement.getDatePaiement() : null;

            int pourcentage = employe.getSalaire().compareTo(BigDecimal.ZERO) > 0
                    ? montantVerse.multiply(BigDecimal.valueOf(100))
                        .divide(employe.getSalaire(), 0, RoundingMode.DOWN)
                        .min(BigDecimal.valueOf(100))
                        .intValue()
                    : 0;

            return new RecapLigne(employe, paye, montantVerse, pourcentage, dernierVersement);
        }).toList();
    }

    public String formatMoisLabel(LocalDate mois) {
        YearMonth ym = YearMonth.from(mois);
        String moisNom = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
        return moisNom.substring(0, 1).toUpperCase() + moisNom.substring(1) + " " + ym.getYear();
    }

    private void verifierPosterieurEmbauche(Employe employe, LocalDate premierJourMois, LocalDate dateVersement) {
        LocalDate embauche = employe.getDateEmbauche();
        if (embauche == null) {
            return; // sécurité : ne devrait pas arriver, le champ est obligatoire à la création
        }

        LocalDate premierJourMoisEmbauche = embauche.withDayOfMonth(1);

        if (premierJourMois.isBefore(premierJourMoisEmbauche)) {
            throw new IllegalArgumentException(
                    "Impossible de verser un salaire pour " + formatMoisLabel(premierJourMois)
                            + " : " + employe.getPrenom() + " " + employe.getNom()
                            + " a été embauché(e) le " + embauche + ".");
        }

        if (dateVersement.isBefore(embauche)) {
            throw new IllegalArgumentException(
                    "La date du versement (" + dateVersement + ") ne peut pas être antérieure "
                            + "à la date d'embauche (" + embauche + ").");
        }
    }

    private void verifierNeDepassePasSalaireDu(Employe employe, BigDecimal montantDejaVerse, BigDecimal montantVerse) {
        BigDecimal salaireDu = employe.getSalaire();

        if (montantDejaVerse.compareTo(salaireDu) >= 0) {
            throw new IllegalArgumentException(
                    "Le salaire de " + employe.getPrenom() + " " + employe.getNom()
                            + " est déjà entièrement payé pour ce mois (" + montantDejaVerse + " Ar versés sur "
                            + salaireDu + " Ar dus).");
        }

        BigDecimal nouveauCumul = montantDejaVerse.add(montantVerse);
        if (nouveauCumul.compareTo(salaireDu) > 0) {
            BigDecimal restant = salaireDu.subtract(montantDejaVerse);
            throw new IllegalArgumentException(
                    "Le montant saisi dépasse le salaire dû. Il reste " + restant
                            + " Ar à verser pour " + employe.getPrenom() + " " + employe.getNom() + ".");
        }
    }

    /** Une ligne du récap mensuel : employé, statut, montant versé, % payé, dernière date. */
    public record RecapLigne(Employe employe, boolean paye, BigDecimal montantVerse, int pourcentage, LocalDate dernierVersement) {
    }
}