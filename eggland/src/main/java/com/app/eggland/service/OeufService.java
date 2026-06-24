package com.app.eggland.service;

import com.app.eggland.repository.OeufProductionRepository;
import com.app.eggland.repository.OeufStatutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service partagé pour les règles de stock d'œufs.
 */
@Service
public class OeufService {

    @Autowired
    private OeufProductionRepository oeufProductionRepository;

    @Autowired
    private OeufStatutRepository oeufStatutRepository;

    /**
     * Stock = totalité produite - œufs cassés, consommés ou vendus.
     */
    public Integer getStockDisponible() {
        long quantiteProduite = oeufProductionRepository.sumQuantiteTotale();
        long quantiteIndisponible = oeufStatutRepository.sumQuantiteIndisponible();
        long stock = quantiteProduite - quantiteIndisponible;

        if (stock < 0) {
            throw new IllegalStateException(
                    "Le stock d'œufs est incohérent : les sorties dépassent la production.");
        }
        return Math.toIntExact(stock);
    }
}
