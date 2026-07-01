package com.app.eggland.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.Lot;
import com.app.eggland.model.Mort;
import com.app.eggland.model.Reforme;
import com.app.eggland.repository.ConfigurationRepository;
import com.app.eggland.repository.MortRepository;
import com.app.eggland.repository.ReformeRepository;

@Service
public class MortService {

    @Autowired
    private MortRepository mortRepository;

    @Autowired
    private ReformeRepository reformeRepository;

    @Autowired
    private LotService lotService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private NotificationService notificationService;

    public Integer getTotalMorts(Integer lotId, LocalDate debut, LocalDate fin) {
        if (lotId != null && lotId > 0) {
            return mortRepository.sumByLotIdAndDateBetween(lotId, debut, fin);
        }
        return mortRepository.sumByDateBetween(debut, fin);
    }

    public Integer getVivants(Integer lotId) {
        if (lotId != null && lotId > 0) {
            Lot lot = lotService.findById(lotId);
            if (lot == null) return 0;
            int totalMorts = mortRepository.sumByLotId(lotId);
            int totalReformes = reformeRepository.sumByLotId(lotId);
            int vivants = lot.getNombreInitial() - totalMorts - totalReformes;
            return Math.max(vivants, 0);
        }
        List<Lot> lotsActifs = lotService.getAllLotsActifs();
        int totalInitial = lotsActifs.stream().mapToInt(Lot::getNombreInitial).sum();
        int totalMorts = mortRepository.findAll().stream().mapToInt(Mort::getNombre).sum();
        int totalReformes = reformeRepository.findAll().stream().mapToInt(Reforme::getNombre).sum();
        int vivants = totalInitial - totalMorts - totalReformes;
        return Math.max(vivants, 0);
    }

    public void verifierSeuil(Integer lotId, Integer totalMorts) {
        if (lotId == null || lotId <= 0 || totalMorts == null) return;
        var config = configurationRepository.findById(1).orElse(null);
        if (config == null) return;
        if (totalMorts > config.getSeuilMort()) {
            notificationService.creer("MORTALITE",
                    "Seuil atteint : " + totalMorts + " morts dans le lot " + lotId);
        }
    }

    public List<Lot> getAllLots() {
        return lotService.getAllLots();
    }
}
