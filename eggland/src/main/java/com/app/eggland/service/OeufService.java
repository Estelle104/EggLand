package com.app.eggland.service;

import com.app.eggland.repository.OeufProductionRepository;
import com.app.eggland.repository.OeufStatutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
}
