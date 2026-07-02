package com.app.eggland.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.repository.LivraisonRepository;
import com.app.eggland.repository.OeufProductionRepository;
import com.app.eggland.repository.VenteRepository;

@Service
public class DashboardService {

    @Autowired
    private OeufProductionRepository oeufProductionRepository;

    @Autowired
    private VenteRepository venteRepository;

    @Autowired
    private LivraisonRepository livraisonRepository;

    @Autowired
    private OeufService oeufService;

    @Autowired
    private FinanceService financeService;

    public Integer getOeufsJour() {
        Integer total = oeufProductionRepository.sumQuantiteByDate(LocalDate.now());
        return total != null ? total : 0;
    }

    public Integer getStockDisponible() {
        return oeufService.getStockDisponible();
    }

    public BigDecimal getVentesJour() {
        BigDecimal total = venteRepository.sumTotalByDate(LocalDate.now());
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getBeneficeJour() {
        LocalDate today = LocalDate.now();
        return financeService.getBeneficeNet(today, today);
    }

    public BigDecimal getBeneficeMois() {
        LocalDate now = LocalDate.now();
        LocalDate debut = now.withDayOfMonth(1);
        LocalDate fin = now.withDayOfMonth(now.lengthOfMonth());
        return financeService.getBeneficeNet(debut, fin);
    }

    public long getLivraisonsEnCours() {
        return livraisonRepository.countByStatutCode("en_cours");
    }
}
