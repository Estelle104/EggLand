package com.app.eggland.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.eggland.model.Client;
import com.app.eggland.model.DetailVente;
import com.app.eggland.model.ProduitVente;
import com.app.eggland.model.StatutVente;
import com.app.eggland.model.Vente;
import com.app.eggland.repository.DetailVenteRepository;
import com.app.eggland.repository.ProduitVenteRepository;
import com.app.eggland.repository.StatutVenteRepository;
import com.app.eggland.repository.VenteRepository;

@Service
public class VenteService {

    @Autowired private VenteRepository        venteRepository;
    @Autowired private DetailVenteRepository  detailVenteRepository;
    @Autowired private ProduitVenteRepository produitVenteRepository;
    @Autowired private StatutVenteRepository  statutVenteRepository;
    @Autowired private MvtArgentService       mvtArgentService;
    @Autowired private OeufService            oeufService;
    @Autowired private LotService             lotService;

    
    public void saveVente(Vente vente) { venteRepository.save(vente); }

    public List<Vente> listeVente() { return venteRepository.findAll(); }

    public List<StatutVente> listeStatutVente() { return statutVenteRepository.findAll(); }

    public List<Vente> filtrerVentes(Integer clientId, Integer statutId,
                                      LocalDate dateDebut, LocalDate dateFin) {
        boolean hasClient = clientId != null;
        boolean hasStatut = statutId != null;
        boolean hasDates  = dateDebut != null || dateFin != null;
        LocalDate debut = dateDebut != null ? dateDebut : LocalDate.of(2000, 1, 1);
        LocalDate fin   = dateFin   != null ? dateFin   : LocalDate.of(2100, 12, 31);

        if (hasClient && hasStatut && hasDates)
            return venteRepository.findByClientIdAndStatutIdAndDateBetweenOrderByDateDesc(clientId, statutId, debut, fin);
        if (hasClient && hasStatut)
            return venteRepository.findByClientIdAndStatutIdOrderByDateDesc(clientId, statutId);
        if (hasClient && hasDates)
            return venteRepository.findByClientIdAndDateBetweenOrderByDateDesc(clientId, debut, fin);
        if (hasStatut && hasDates)
            return venteRepository.findByStatutIdAndDateBetweenOrderByDateDesc(statutId, debut, fin);
        if (hasClient)
            return venteRepository.findByClientIdOrderByDateDesc(clientId);
        if (hasStatut)
            return venteRepository.findByStatutIdOrderByDateDesc(statutId);
        if (hasDates)
            return venteRepository.findByDateBetweenOrderByDateDesc(debut, fin);
        return venteRepository.findAllByOrderByDateDesc();
    }

    public void supprimerVente(int id) {
        List<DetailVente> details = detailVenteRepository.findByVenteId(id);
        detailVenteRepository.deleteAll(details);
        venteRepository.deleteById(id);
    }

    public Vente trouverVenteParId(int id) {
        return venteRepository.findById(id).orElse(null);
    }

    public List<ProduitVente> listeProduitVente() { return produitVenteRepository.findAll(); }

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

        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente produit = trouverProduitVenteParId(produitIds.get(i));
            if (produit == null) continue;
            BigDecimal qte  = quantites.get(i);
            BigDecimal prix = prixUnitaires.get(i);
            total = total.add(qte.multiply(prix));

            if ("oeuf".equalsIgnoreCase(produit.getCode())) {
                int stockDispo = oeufService.getStockDisponible();
                if (qte.intValue() > stockDispo) {
                    throw new RuntimeException(
                        "Stock d'œufs insuffisant. Disponible : " + stockDispo
                        + " | Demandé : " + qte.intValue());
                }
                oeufService.retirerDuStock(qte.intValue());
            }
        }

        StatutVente statut = statutVenteRepository.findByCode("paye")
            .orElseThrow(() -> new RuntimeException("Statut 'paye' introuvable en base"));

        Vente vente = Vente.builder()
            .client(client).date(LocalDate.now()).total(total).statut(statut).build();
        venteRepository.save(vente);

        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente produit = trouverProduitVenteParId(produitIds.get(i));
            if (produit == null) continue;

            DetailVente detail = DetailVente.builder()
                .vente(vente).client(client).produit(produit)
                .quantite(quantites.get(i)).prixUnitaire(prixUnitaires.get(i)).build();
            detailVenteRepository.save(detail);

            if ("poule".equalsIgnoreCase(produit.getCode())) {
                Integer lotId = (lotIds != null && i < lotIds.size()) ? lotIds.get(i) : null;
                if (lotId != null) lotService.reformerUnLot(lotId, LocalDate.now());
            }
        }

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

        List<DetailVente> anciensDetails = detailVenteRepository.findByVenteId(venteId);

        int ancienneQteOeuf = 0;
        for (DetailVente d : anciensDetails) {
            if ("oeuf".equalsIgnoreCase(d.getProduit().getCode()))
                ancienneQteOeuf += d.getQuantite().intValue();
        }

        int nouvelleQteOeuf = 0;
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente p = trouverProduitVenteParId(produitIds.get(i));
            if (p == null) continue;
            BigDecimal qte = quantites.get(i);
            total = total.add(qte.multiply(prixUnitaires.get(i)));
            if ("oeuf".equalsIgnoreCase(p.getCode())) nouvelleQteOeuf += qte.intValue();
        }

        int diff = nouvelleQteOeuf - ancienneQteOeuf;
        if (diff > 0) {
            int stock = oeufService.getStockDisponible();
            if (diff > stock) throw new RuntimeException(
                "Stock insuffisant pour ajouter " + diff + " œufs. Disponible : " + stock);
            oeufService.retirerDuStock(diff);
        } else if (diff < 0) {
            oeufService.ajouterAuStock(Math.abs(diff));
        }

        detailVenteRepository.deleteAll(anciensDetails);

        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente produit = trouverProduitVenteParId(produitIds.get(i));
            if (produit == null) continue;
            DetailVente detail = DetailVente.builder()
                .vente(vente).client(client).produit(produit)
                .quantite(quantites.get(i)).prixUnitaire(prixUnitaires.get(i)).build();
            detailVenteRepository.save(detail);
            if ("poule".equalsIgnoreCase(produit.getCode())) {
                Integer lotId = (lotIds != null && i < lotIds.size()) ? lotIds.get(i) : null;
                if (lotId != null) lotService.reformerUnLot(lotId, LocalDate.now());
            }
        }

        vente.setClient(client);
        vente.setTotal(total);
        venteRepository.save(vente);
        mvtArgentService.creerEntree(total, LocalDate.now(), "Modification Vente #" + vente.getId());
    }
}
