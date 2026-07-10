package com.app.eggland.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.Lot;
import com.app.eggland.model.Mort;
import com.app.eggland.model.Reforme;
import com.app.eggland.repository.LotRepository;
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
    private NotificationService notificationService;

    @Autowired
    private LotRepository lotRepository;

    public Integer getTotalMortsParLot(Integer lotId) {
        if (lotId == null || lotId <= 0)
            return 0;
        Long total = mortRepository.sumByLotId(lotId);
        return total != null ? total.intValue() : 0;
    }

    public Integer getTotalMorts(Integer lotId, LocalDate debut, LocalDate fin) {
        Long total;
        boolean hasLot = lotId != null && lotId > 0;
        boolean hasDateFilter = debut != null || fin != null;

        if (!hasDateFilter) {
            if (hasLot) {
                total = mortRepository.sumByLotId(lotId);
            } else {
                total = mortRepository.findAll().stream()
                        .mapToLong(Mort::getNombre)
                        .sum();
            }
            return total != null ? total.intValue() : 0;
        }

        LocalDate dateDebut = debut != null ? debut : LocalDate.of(1900, 1, 1);
        LocalDate dateFin = fin != null ? fin : LocalDate.of(9999, 12, 31);

        if (hasLot) {
            total = mortRepository.sumByLotIdAndDateBetween(lotId, dateDebut, dateFin);
        } else {
            total = mortRepository.sumByDateBetween(dateDebut, dateFin);
        }
        return total != null ? total.intValue() : 0;
    }

    public Integer getTotalInitial(Integer lotId) {
        if (lotId != null && lotId > 0) {
            Lot lot = lotService.findById(lotId);
            return lot != null && lot.getNombreInitial() != null ? lot.getNombreInitial() : 0;
        }

        return lotService.getAllLots().stream()
                .map(Lot::getNombreInitial)
                .filter(nombre -> nombre != null)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public Integer getVivants(Integer lotId) {
        if (lotId != null && lotId > 0) {
            Lot lot = lotService.findById(lotId);
            if (lot == null)
                return 0;
            Long totalMorts = mortRepository.sumByLotId(lotId);
            Long totalReformes = reformeRepository.sumByLotId(lotId);
            int morts = totalMorts != null ? totalMorts.intValue() : 0;
            int reformes = totalReformes != null ? totalReformes.intValue() : 0;
            int vivants = lot.getNombreInitial() - morts - reformes;
            return Math.max(vivants, 0);
        }
        List<Lot> tousLesLots = lotService.getAllLots();
        int totalInitial = tousLesLots.stream().mapToInt(Lot::getNombreInitial).sum();
        int totalMorts = mortRepository.findAll().stream().mapToInt(Mort::getNombre).sum();
        int totalReformes = reformeRepository.findAll().stream().mapToInt(Reforme::getNombre).sum();
        int vivants = totalInitial - totalMorts - totalReformes;
        return Math.max(vivants, 0);
    }

    public void verifierSeuil(Integer lotId, Integer totalMorts) {
        if (lotId == null || lotId <= 0 || totalMorts == null)
            return;
        Lot lot = lotService.findById(lotId);
        if (lot == null || lot.getNombreInitial() == null || lot.getNombreInitial() <= 0)
            return;
        int seuil = lot.getNombreInitial() / 2;
        if (totalMorts >= seuil) {
            notificationService.creer("MORTALITE",
                    "Seuil atteint : " + totalMorts + " morts dans le lot " + lotId);
        }
    }

    public List<Lot> getAllLots() {
        return lotService.getAllLots();
    }

    /// CRUD
    public Mort save(Mort mort) {
        if (mort.getDate() == null) {
            mort.setDate(LocalDate.now());
        }
        return mortRepository.save(mort);
    }

    public List<Mort> findAll() {
        return mortRepository.findAll();
    }

    public Optional<Mort> findById(Integer id) {
        return mortRepository.findById(id);
    }

    public void deleteById(Integer id) {
        mortRepository.deleteById(id);
    }

    // fonction lot temporaire (git conflict)
    public List<Lot> findAllLotTemporary() {
        return lotRepository.findAll();
    }

    public List<Mort> findByLot(Lot lot) {
        return mortRepository.findByLot(lot);
    }

    // nombre de poule vivant dans un lot
    public Integer getNombreActuel(Lot lot) {
        Integer totalMorts = mortRepository.sumMortalityByLot(lot);
        return lot.getNombreInitial() - totalMorts;
    }

    // nombre total mort
    public Integer getTotalMort() {
        List<Lot> lots = lotRepository.findAll();
        Integer total = 0;
        for (Lot lot : lots)
            total += mortRepository.sumMortalityByLot(lot);

        return total;
    }

    // mort par lot
    public Integer getNombreMort(Lot lot) {
        return mortRepository.sumMortalityByLot(lot);
    }
}
