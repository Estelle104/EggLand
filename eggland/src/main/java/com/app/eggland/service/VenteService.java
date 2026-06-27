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


    public void saveVente(Vente vente) {
        venteRepository.save(vente);
    }

    public List<Vente> listeVente() {
        return venteRepository.findAll();
    }

    public void supprimerVente(int id) {
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
    public void enregistrerVente(int clientId,
                                  List<Integer> produitIds,
                                  List<Integer> lotIds,
                                  List<BigDecimal> quantites,
                                  List<BigDecimal> prixUnitaires,
                                  Client client) {

        BigDecimal total = BigDecimal.ZERO;

        // --- Calcul du total et validation stock oeufs ---
        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente produit = trouverProduitVenteParId(produitIds.get(i));
            if (produit == null) continue;

            BigDecimal qte = quantites.get(i);
            BigDecimal prix = prixUnitaires.get(i);
            total = total.add(qte.multiply(prix));

            // Vérifier stock si produit = oeuf
            if ("oeuf".equalsIgnoreCase(produit.getCode())) {
                int stockDispo = oeufService.getStockDisponible();
                oeufService.retirerDuStock(qte.intValue());
                if (qte.intValue() > stockDispo) {
                    throw new RuntimeException(
                        "Stock d'œufs insuffisant. Disponible : " + stockDispo
                        + " | Demandé : " + qte.intValue()
                    );
                }
            }
        }

        // --- Créer et sauvegarder la Vente ---
        StatutVente statut = statutVenteRepository.findByCode("paye")
            .orElseThrow(() -> new RuntimeException("Statut 'validee' introuvable en base"));

        Vente vente = Vente.builder()
            .client(client)
            .date(LocalDate.now())
            .total(total)
            .statut(statut)
            .build();
        venteRepository.save(vente);

        // --- Sauvegarder chaque ligne de détail ---
        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente produit = trouverProduitVenteParId(produitIds.get(i));
            if (produit == null) continue;

            DetailVente detail = DetailVente.builder()
                .vente(vente)
                .client(client)
                .produit(produit)
                .quantite(quantites.get(i))
                .prixUnitaire(prixUnitaires.get(i))
                .build();
            detailVenteRepository.save(detail);
            // Si produit = poulet → réformer le lot
            if ("poule".equalsIgnoreCase(produit.getCode())) {
                Integer lotId = (lotIds != null && i < lotIds.size()) ? lotIds.get(i) : null;
                if (lotId != null) {
                    lotService.reformerUnLot(lotId, LocalDate.now());
                }
            }
        }

        // --- Mouvement d'argent : entrée catégorie "vente" ---
        mvtArgentService.creerEntree(total, LocalDate.now(), "vente");
    }
@Transactional
    public void enregistrerModificationVente(int venteId,
                                             List<Integer> produitIds,
                                             List<Integer> lotIds,
                                             List<BigDecimal> quantites,
                                             List<BigDecimal> prixUnitaires,
                                             Client client) {

        Vente vente = venteRepository.findById(venteId)
            .orElseThrow(() -> new RuntimeException("Vente introuvable"));

        // Récupérer les lignes de détails actuelles avant modification
        List<DetailVente> anciensDetails = detailVenteRepository.findByVenteId(venteId);

        // On va d'abord identifier l'ancienne quantité d'œufs pour calculer la différence
        int ancienneQuantiteOeuf = 0;
        for (DetailVente detail : anciensDetails) {
            if ("oeuf".equalsIgnoreCase(detail.getProduit().getCode())) {
                ancienneQuantiteOeuf += detail.getQuantite().intValue();
            }
        }

        // On calcule la nouvelle quantité d'œufs demandée dans le formulaire
        int nouvelleQuantiteOeuf = 0;
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente produit = trouverProduitVenteParId(produitIds.get(i));
            if (produit == null) continue;

            BigDecimal qte = quantites.get(i);
            BigDecimal prix = prixUnitaires.get(i);
            total = total.add(qte.multiply(prix));

            if ("oeuf".equalsIgnoreCase(produit.getCode())) {
                nouvelleQuantiteOeuf += qte.intValue();
            }
        }

        int differenceOeuf = nouvelleQuantiteOeuf - ancienneQuantiteOeuf;

        if (differenceOeuf > 0) {
            // L'utilisateur a augmenté la quantité d'œufs : on valide le stock et on retire le surplus
            int stockDispo = oeufService.getStockDisponible();
            if (differenceOeuf > stockDispo) {
                throw new RuntimeException("Stock d'œufs insuffisant pour ajouter " + differenceOeuf + " œufs supplémentaires. Disponible : " + stockDispo);
            }
            oeufService.retirerDuStock(differenceOeuf);
        } else if (differenceOeuf < 0) {
            // L'utilisateur a diminué la quantité d'œufs : on restitue la différence au stock
            oeufService.ajouterAuStock(Math.abs(differenceOeuf));
        }

        // --- MISE À JOUR DES DÉTAILS DE LA VENTE ---
        // Pour éviter les conflits d'indexation complexes, on remplace proprement les lignes de détails
        detailVenteRepository.deleteAll(anciensDetails);

        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente produit = trouverProduitVenteParId(produitIds.get(i));
            if (produit == null) continue;

            DetailVente detail = DetailVente.builder()
                .vente(vente)
                .client(client)
                .produit(produit)
                .quantite(quantites.get(i))
                .prixUnitaire(prixUnitaires.get(i))
                .build();
            detailVenteRepository.save(detail);

            // Si c'est une poule et qu'un lot est sélectionné (nouveau ou modifié)
            if ("poule".equalsIgnoreCase(produit.getCode())) {
                Integer lotId = (lotIds != null && i < lotIds.size()) ? lotIds.get(i) : null;
                if (lotId != null) {
                    lotService.reformerUnLot(lotId, LocalDate.now());
                }
            }
        }

        // Mettre à jour l'entité Vente parente
        vente.setClient(client);
        vente.setTotal(total);
        venteRepository.save(vente);

        // Mouvement d'argent : Mise à jour (on enregistre la régularisation ou le nouveau montant total)
        mvtArgentService.creerEntree(total, LocalDate.now(), "Modification Vente #" + vente.getId());
    }
}