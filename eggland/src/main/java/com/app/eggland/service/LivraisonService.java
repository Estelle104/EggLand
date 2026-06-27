package com.app.eggland.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.eggland.model.Livraison;
import com.app.eggland.model.StatutLivraison;
import com.app.eggland.model.Vente;
import com.app.eggland.repository.LivraisonRepository;
import com.app.eggland.repository.StatutLivraisonRepository;
import com.app.eggland.repository.VenteRepository;

@Service
public class LivraisonService {

    @Autowired
    private LivraisonRepository livraisonRepository;

    @Autowired
    private VenteRepository venteRepository;

    @Autowired
    private StatutLivraisonRepository statutLivraisonRepository;

    @Autowired
    private MvtArgentService mvtArgentService;

    public Livraison saveLivraison(Livraison livraison) {
        return livraisonRepository.save(livraison);
    }

    public List<Livraison> listeLivraison() {
        return livraisonRepository.findAll();
    }

    public Livraison trouverLivraisonParId(int id) {
        return livraisonRepository.findById(id).orElse(null);
    }

    @Transactional
    public Livraison creerLivraisonDepuisVente(int venteId,
                                               LocalDate dateLivraison,
                                               String adresseLivraison,
                                               BigDecimal fraisLivraison,
                                               String statutCode) {

        Vente vente = venteRepository.findById(venteId)
                .orElseThrow(() -> new RuntimeException("Vente introuvable"));

        if (dateLivraison == null) {
            dateLivraison = LocalDate.now();
        }

        if (fraisLivraison == null) {
            fraisLivraison = BigDecimal.ZERO;
        }

        String statutRecherche = (statutCode == null || statutCode.isBlank()) ? "en_attente" : statutCode;
        StatutLivraison statut = statutLivraisonRepository.findByCode(statutRecherche)
                .orElseThrow(() -> new RuntimeException("Statut de livraison introuvable : " + statutRecherche));

        Livraison livraison = Livraison.builder()
                .vente(vente)
                .client(vente.getClient())
                .dateLivraison(dateLivraison)
                .adresseLivraison(adresseLivraison)
                .statut(statut)
                .fraisLivraison(fraisLivraison)
                .build();

        Livraison saved = livraisonRepository.save(livraison);

        if (fraisLivraison.compareTo(BigDecimal.ZERO) > 0) {
            mvtArgentService.creerSortie(fraisLivraison, saved.getDateLivraison(), "livraison");
        }

        return saved;
    }

    @Transactional
    public Livraison changerStatutLivraison(int livraisonId, String statutCode) {
        Livraison livraison = livraisonRepository.findById(livraisonId)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable"));

        StatutLivraison statut = statutLivraisonRepository.findByCode(statutCode)
                .orElseThrow(() -> new RuntimeException("Statut de livraison introuvable : " + statutCode));

        livraison.setStatut(statut);
        return livraisonRepository.save(livraison);
    }
}
