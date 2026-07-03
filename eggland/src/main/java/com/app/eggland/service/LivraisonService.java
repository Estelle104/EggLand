package com.app.eggland.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.eggland.model.Batiment;
import com.app.eggland.model.Client;
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

    public List<Livraison> filtrerLivraisons(LocalDate dateDebut, LocalDate dateFin) {
        boolean hasDebut = dateDebut != null;
        boolean hasFin   = dateFin   != null;

        if (hasDebut && hasFin) {
            return livraisonRepository.findByDateLivraisonBetweenOrderByDateLivraisonDesc(dateDebut, dateFin);
        }
        if (hasDebut) {
            return livraisonRepository.findByDateLivraisonAfterOrderByDateLivraisonDesc(dateDebut);
        }
        if (hasFin) {
            return livraisonRepository.findByDateLivraisonBeforeOrderByDateLivraisonDesc(dateFin);
        }
        return livraisonRepository.findAllByOrderByDateLivraisonDesc();
    }

    @Transactional
    public void supprimerLivraison(int id) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable avec l'ID : " + id));

        if ("livre".equals(livraison.getStatut().getCode())) {
            // même si livré, on peut supprimer
        }
        livraisonRepository.delete(livraison);
    }

    public List<Livraison> listerLivraisonEnCoursPourClient(String emailClient) {
        return livraisonRepository.findByClientEmailAndStatutCode(emailClient, "en_cours");
    }

    public Integer compterLivraisonEnCoursPourClient(List<Livraison> livraisons) {
        return livraisons.size();
    }

    public List<Vente> obtenirVentesNonLivrees() {
        return venteRepository.findVentesNonLivrees();
    }

    @Transactional
    public Livraison creerLivraison(Vente vente, Client client, LocalDate dateLivraison, String adresseLivraison, BigDecimal fraisLivraison, String statutCode) {
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
                .client(client)
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

    public List<Livraison> filtrerLivraisonsParClient(String nomClient, LocalDate dateDebut, LocalDate dateFin) {
        List<Livraison> livraisons = livraisonRepository.findByClientNomContainingIgnoreCase(nomClient);
        
        if (dateDebut != null || dateFin != null) {
            LocalDate debut = dateDebut != null ? dateDebut : LocalDate.of(2000, 1, 1);
            LocalDate fin = dateFin != null ? dateFin : LocalDate.of(2100, 12, 31);
            
            livraisons = livraisons.stream()
                .filter(l -> !l.getDateLivraison().isBefore(debut) && !l.getDateLivraison().isAfter(fin))
                .toList();
        }
        
        return livraisons;
    }

    /*méthode pour la pagination*/
    public List<Livraison> getPage(List<Livraison> livraisons, int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, livraisons.size());
        if (start > end) {
            return List.of(); // Retourne une liste vide si la page demandée est hors limites
        }
        return livraisons.subList(start, end);
    }
}
