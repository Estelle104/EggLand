package com.app.eggland.service;

import com.app.eggland.model.OeufStatut;
import com.app.eggland.repository.OeufProductionRepository;
import com.app.eggland.repository.OeufStatutRepository;

import java.math.BigDecimal;
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

    public Integer getStockDisponible() {
        Integer quantiteProduite = oeufProductionRepository.sumQuantiteTotale();
        Integer quantiteIndisponible = oeufStatutRepository.sumQuantiteIndisponible();
        Integer stock = quantiteProduite - quantiteIndisponible;

        if (stock < 0) {
            throw new IllegalStateException(
                    "Le stock d'œufs est incohérent : les sorties dépassent la production.");
        }
        return Math.toIntExact(stock);
    }
  @Transactional
    public void retirerDuStock(int quantite) {

        List<OeufStatut> stocks = oeufStatutRepository
                .findByStatutCodeOrderByProductionDateAsc("valide");

        int restant = quantite;

        for (OeufStatut stock : stocks) {

            if (restant <= 0) {
                break;
            }

            if (stock.getQuantite() <= restant) {

                restant -= stock.getQuantite();
                stock.setQuantite(0);

            } else {

                stock.setQuantite(stock.getQuantite() - restant);
                restant = 0;
            }

            oeufStatutRepository.save(stock);
        }

        if (restant > 0) {
            throw new RuntimeException("Stock insuffisant.");
        }
    } 

    @Transactional
    public void ajouterAuStock(int quantite) {
        if (quantite <= 0) return;
        List<OeufStatut> stocks = oeufStatutRepository
                .findByStatutCodeOrderByProductionDateAsc("valide");

        if (stocks.isEmpty()) {
            throw new RuntimeException("Aucun enregistrement de stock valide trouvé pour restituer les œufs.");
        }
        OeufStatut premierStock = stocks.get(0);
        premierStock.setQuantite(premierStock.getQuantite() + quantite);
        oeufStatutRepository.save(premierStock);
    }
}
