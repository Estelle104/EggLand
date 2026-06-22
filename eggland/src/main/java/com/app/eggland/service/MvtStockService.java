package com.app.eggland.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.MvtStock;
import com.app.eggland.model.TypeMvt;
import com.app.eggland.repository.MvtStockRepository;
import com.app.eggland.repository.TypeMvtRepository;

@Service
public class MvtStockService {
    @Autowired
    private MvtStockRepository mvtStockRepository;

    @Autowired
    private TypeMvtRepository typeMvtRepository;

    public List<MvtStock> findAll() {
        return mvtStockRepository.findAll();
    }

    public Optional<MvtStock> findById(Integer id) {
        return mvtStockRepository.findById(id);
    }

    public MvtStock save(MvtStock mvtStock) {
        return mvtStockRepository.save(mvtStock);
    }

    public void deleteById(Integer id) {
        mvtStockRepository.deleteById(id);
    }

    public TypeMvt getTypeEntree() {
        return typeMvtRepository.findByCode("entree")
                .orElseThrow(() -> new RuntimeException("Type de mouvement 'entree' introuvable"));
    }

    public TypeMvt getTypeSortie() {
        return typeMvtRepository.findByCode("sortie")
                .orElseThrow(() -> new RuntimeException("Type de mouvement 'sortie' introuvable"));
    }

    public BigDecimal calculerStockActuel(Integer nourritureId) {
        BigDecimal entrees = mvtStockRepository.sumQuantiteByNourritureAndType(nourritureId, getTypeEntree());
        BigDecimal sorties = mvtStockRepository.sumQuantiteByNourritureAndType(nourritureId, getTypeSortie());
        return entrees.subtract(sorties);
    }
}
