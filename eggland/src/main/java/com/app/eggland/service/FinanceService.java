package com.app.eggland.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.Lot;
import com.app.eggland.repository.MvtArgentRepository;
import com.app.eggland.repository.LotRepository;

@Service
public class FinanceService {

    @Autowired
    private MvtArgentRepository mvtArgentRepository;

    @Autowired
    private LotRepository lotRepository;

    public BigDecimal getTotalRecettes() {
        return mvtArgentRepository.sumMontantByTypeCode("entree");
    }

    public BigDecimal getTotalRecettes(LocalDate debut, LocalDate fin) {
        return mvtArgentRepository.sumMontantByTypeCodeBetweenDates("entree", debut, fin);
    }

    public BigDecimal getTotalDepenses() {
        return mvtArgentRepository.sumMontantByTypeCode("sortie");
    }

    public BigDecimal getTotalDepenses(LocalDate debut, LocalDate fin) {
        return mvtArgentRepository.sumMontantByTypeCodeBetweenDates("sortie", debut, fin);
    }

    public BigDecimal getBeneficeNet() {
        return getTotalRecettes().subtract(getTotalDepenses());
    }

    public BigDecimal getBeneficeNet(LocalDate debut, LocalDate fin) {
        return getTotalRecettes(debut, fin).subtract(getTotalDepenses(debut, fin));
    }

    public Map<String, BigDecimal> getResumeFinancier() {
        Map<String, BigDecimal> resume = new LinkedHashMap<>();
        resume.put("totalRecettes", getTotalRecettes());
        resume.put("totalDepenses", getTotalDepenses());
        resume.put("beneficeNet", getBeneficeNet());
        return resume;
    }

    public Map<String, BigDecimal> getResumeFinancier(LocalDate debut, LocalDate fin) {
        Map<String, BigDecimal> resume = new LinkedHashMap<>();
        resume.put("totalRecettes", getTotalRecettes(debut, fin));
        resume.put("totalDepenses", getTotalDepenses(debut, fin));
        resume.put("beneficeNet", getBeneficeNet(debut, fin));
        return resume;
    }

    public Map<String, BigDecimal> getRecettesParCategorie() {
        List<Object[]> results = mvtArgentRepository.sumMontantByCategorie("entree");
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (BigDecimal) row[1],
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public Map<String, BigDecimal> getRecettesParCategorie(LocalDate debut, LocalDate fin) {
        List<Object[]> results = mvtArgentRepository.sumMontantByCategorieBetweenDates("entree", debut, fin);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (BigDecimal) row[1],
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public Map<String, BigDecimal> getDepensesParCategorie() {
        List<Object[]> results = mvtArgentRepository.sumMontantByCategorie("sortie");
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (BigDecimal) row[1],
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public Map<String, BigDecimal> getDepensesParCategorie(LocalDate debut, LocalDate fin) {
        List<Object[]> results = mvtArgentRepository.sumMontantByCategorieBetweenDates("sortie", debut, fin);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (BigDecimal) row[1],
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public Map<Lot, BigDecimal> getCoutRevientParLot() {
        List<Object[]> results = mvtArgentRepository.sumMontantByLotForSorties();
        return results.stream()
                .filter(row -> row[0] != null)
                .collect(Collectors.toMap(
                        row -> lotRepository.findById((Integer) row[0]).orElse(null),
                        row -> (BigDecimal) row[1],
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public Map<String, BigDecimal> getRecettesMensuelles12Mois() {
        LocalDate fin = LocalDate.now().withDayOfMonth(1);
        LocalDate debut = fin.minusMonths(11);
        List<Object[]> results = mvtArgentRepository.sumMontantByMoisBetweenDates("entree", debut, fin);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (int i = 0; i < 12; i++) {
            LocalDate month = debut.plusMonths(i);
            String key = month.getYear() + "-" + String.format("%02d", month.getMonthValue());
            map.put(key, BigDecimal.ZERO);
        }
        results.forEach(row -> map.put((String) row[0], (BigDecimal) row[1]));
        return map;
    }

    public Map<String, BigDecimal> getDepensesMensuelles12Mois() {
        LocalDate fin = LocalDate.now().withDayOfMonth(1);
        LocalDate debut = fin.minusMonths(11);
        List<Object[]> results = mvtArgentRepository.sumMontantByMoisBetweenDates("sortie", debut, fin);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (int i = 0; i < 12; i++) {
            LocalDate month = debut.plusMonths(i);
            String key = month.getYear() + "-" + String.format("%02d", month.getMonthValue());
            map.put(key, BigDecimal.ZERO);
        }
        results.forEach(row -> map.put((String) row[0], (BigDecimal) row[1]));
        return map;
    }
}
