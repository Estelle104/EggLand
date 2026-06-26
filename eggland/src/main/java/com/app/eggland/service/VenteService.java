package com.app.eggland.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.eggland.model.Client;
import com.app.eggland.model.DetailVente;
import com.app.eggland.model.Lot;
import com.app.eggland.model.ProduitVente;
import com.app.eggland.model.StatutVente;
import com.app.eggland.model.Vente;
import com.app.eggland.repository.DetailVenteRepository;
import com.app.eggland.repository.ProduitVenteRepository;
import com.app.eggland.repository.StatutVenteRepository;
import com.app.eggland.repository.VenteRepository;

@Service
public class VenteService {

    @Autowired
    private VenteRepository venteRepository;

    @Autowired
    private DetailVenteRepository detailVenteRepository;

    @Autowired
    private ProduitVenteRepository produitVenteRepository;

    @Autowired
    private StatutVenteRepository statutVenteRepository;

    @Autowired
    private MvtArgentService mvtArgentService;

    @Autowired
    private OeufService oeufService;

    @Autowired
    private LotService lotService;

    // -------------------------------------------------------
    // CRUD de base
    // -------------------------------------------------------

    public void saveVente(Vente vente) {
        venteRepository.save(vente);
    }

    public List<Vente> listeVente() {
        return venteRepository.findAll();
    }

    public void supprimerVente(int id) {
        // Supprimer d'abord les détails liés
        List<DetailVente> details = detailVenteRepository.findByVenteId(id);
        detailVenteRepository.deleteAll(details);
        venteRepository.deleteById(id);
    }

    public Vente trouverVenteParId(int id) {
        return venteRepository.findById(id).orElse(null);
    }

    public List<ProduitVente> listeProduitVente() {
        return produitVenteRepository.findAll();
    }

    public ProduitVente trouverProduitVenteParId(int id) {
        return produitVenteRepository.findById(id).orElse(null);
    }

    public List<DetailVente> listeDetailVente(int idVente) {
        return detailVenteRepository.findByVenteId(idVente);
    }


    @Transactional
    public Vente enregistrerVente(Client client,
                                  String[] produitIds,
                                  String[] quantites,
                                  String[] prixUnitaires,
                                  String[] lotIds) {

        if (produitIds == null || produitIds.length == 0) {
            throw new IllegalArgumentException("Aucune ligne de vente fournie.");
        }

        BigDecimal total = BigDecimal.ZERO;

        // --- Récupérer le statut "en_cours" (ou premier statut disponible) ---
        StatutVente statut = statutVenteRepository.findAll()
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Aucun statut de vente trouvé en base."));

        // --- Créer la vente ---
        Vente vente = Vente.builder()
                .client(client)
                .date(LocalDate.now())
                .total(BigDecimal.ZERO)   // sera mis à jour après
                .statut(statut)
                .build();
        venteRepository.save(vente);

        // --- Traiter chaque ligne ---
        for (int i = 0; i < produitIds.length; i++) {
            if (produitIds[i] == null || produitIds[i].isBlank()) continue;

            int produitId   = Integer.parseInt(produitIds[i]);
            BigDecimal qte  = new BigDecimal(quantites != null && i < quantites.length ? quantites[i] : "1");
            BigDecimal prix = new BigDecimal(prixUnitaires != null && i < prixUnitaires.length ? prixUnitaires[i] : "0");

            ProduitVente produit = produitVenteRepository.findById(produitId)
                    .orElseThrow(() -> new RuntimeException("Produit introuvable : " + produitId));

            // --- Validation stock œufs ---
            if ("oeuf".equalsIgnoreCase(produit.getCode())) {
                int stockDispo = oeufService.getStockDisponible();
                if (qte.intValue() > stockDispo) {
                    throw new IllegalStateException(
                        "Stock d'œufs insuffisant. Disponible : " + stockDispo + ", demandé : " + qte.intValue());
                }
            }

            // --- Si produit = poulet → réformer le lot ---
            if ("poulet".equalsIgnoreCase(produit.getCode())) {
                if (lotIds != null && i < lotIds.length && lotIds[i] != null && !lotIds[i].isBlank()) {
                    int lotId = Integer.parseInt(lotIds[i]);
                    lotService.reformerUnLot(lotId, LocalDate.now());
                }
            }

            // --- Créer le détail ---
            DetailVente detail = DetailVente.builder()
                    .vente(vente)
                    .client(client)
                    .produit(produit)
                    .quantite(qte)
                    .prixUnitaire(prix)
                    .build();
            detailVenteRepository.save(detail);

            total = total.add(qte.multiply(prix));
        }

        // --- Mettre à jour le total de la vente ---
        vente.setTotal(total);
        venteRepository.save(vente);

        // --- Enregistrer un mouvement d'argent (entrée / vente) ---
        mvtArgentService.creerEntree(total, LocalDate.now(), "vente");

        return vente;
    }
}