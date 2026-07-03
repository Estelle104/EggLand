package com.app.eggland.service;

import com.app.eggland.model.OeufStatut;
import com.app.eggland.model.StatutOeuf;
import com.app.eggland.repository.OeufProductionRepository;
import com.app.eggland.repository.OeufStatutRepository;
import com.app.eggland.repository.StatutOeufRepository;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OeufService {

    @Autowired
    private OeufProductionRepository oeufProductionRepository;

    @Autowired
    private OeufStatutRepository oeufStatutRepository;

    @Autowired
    private StatutOeufRepository statutOeufRepository;

    public Integer getStockDisponible() {
        Long quantiteProduite = oeufProductionRepository.sumQuantiteTotale();
        Long quantiteIndisponible = oeufStatutRepository.sumQuantiteIndisponible();
        long prod = quantiteProduite != null ? quantiteProduite : 0L;
        long indispo = quantiteIndisponible != null ? quantiteIndisponible : 0L;
        long stock = prod - indispo;

        if (stock < 0) {
            throw new IllegalStateException(
                    "Le stock d'œufs est incohérent : les sorties dépassent la production.");
        }
        return (int) stock;
    }


    @Transactional
    public void retirerDuStock(int quantite) {

        List<OeufStatut> stocksValides = oeufStatutRepository
                .findByStatutCodeOrderByProductionDateAsc("valide");

        StatutOeuf statutVendu = statutOeufRepository.findByCode("vendu")
                .orElseThrow(() -> new RuntimeException("Statut 'vendu' introuvable en base"));

        int restant = quantite;

        for (OeufStatut stock : stocksValides) {

            if (restant <= 0) {
                break;
            }

            int quantitePrise = Math.min(stock.getQuantite(), restant);
            if (quantitePrise <= 0) {
                continue;
            }

            stock.setQuantite(stock.getQuantite() - quantitePrise);
            oeufStatutRepository.save(stock);

            OeufStatut vendu = oeufStatutRepository
                    .findByProductionIdAndStatutCode(stock.getProduction().getId(), "vendu")
                    .orElseGet(() -> {
                        OeufStatut nouveau = new OeufStatut();
                        nouveau.setProduction(stock.getProduction());
                        nouveau.setStatut(statutVendu);
                        nouveau.setQuantite(0);
                        return nouveau;
                    });
            vendu.setQuantite(vendu.getQuantite() + quantitePrise);
            oeufStatutRepository.save(vendu);

            restant -= quantitePrise;
        }

        if (restant > 0) {
            throw new RuntimeException("Stock insuffisant.");
        }
    }
    @Transactional
    public void ajouterAuStock(int quantite) {
        if (quantite <= 0) return;

        List<OeufStatut> stocksVendus = oeufStatutRepository
                .findByStatutCodeOrderByProductionDateAsc("vendu");
        Collections.reverse(stocksVendus);

        int restant = quantite;

        for (OeufStatut vendu : stocksVendus) {
            if (restant <= 0) {
                break;
            }

            int quantiteRestituee = Math.min(vendu.getQuantite(), restant);
            if (quantiteRestituee <= 0) {
                continue;
            }

            vendu.setQuantite(vendu.getQuantite() - quantiteRestituee);
            oeufStatutRepository.save(vendu);

            OeufStatut valide = oeufStatutRepository
                    .findByProductionIdAndStatutCode(vendu.getProduction().getId(), "valide")
                    .orElseThrow(() -> new RuntimeException(
                            "Statut 'valide' introuvable pour la production " + vendu.getProduction().getId()));
            valide.setQuantite(valide.getQuantite() + quantiteRestituee);
            oeufStatutRepository.save(valide);

            restant -= quantiteRestituee;
        }

        if (restant > 0) {
            throw new RuntimeException(
                    "Impossible de restituer plus d'œufs qu'il n'en a été vendu.");
        }
    }
}
