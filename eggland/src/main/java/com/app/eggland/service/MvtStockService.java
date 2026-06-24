package com.app.eggland.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.MvtStock;
import com.app.eggland.model.Nourriture;
import com.app.eggland.model.TypeMvt;
import com.app.eggland.repository.MvtStockRepository;
import com.app.eggland.repository.TypeMvtRepository;

@Service
public class MvtStockService {
    @Autowired
    private MvtStockRepository mvtStockRepository;

    @Autowired
    private TypeMvtRepository typeMvtRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MvtArgentService mvtArgentService;

    @Autowired
    private NourritureService nourritureService;

    public List<MvtStock> findAll() {
        return mvtStockRepository.findAllByOrderByDateDesc();
    }

    //pour afficher l'historique des mouvements de stock d'une nourriture spécifique, on trie par date décroissante
    public List<MvtStock> findByNourritureId(Integer nourritureId) {
        return mvtStockRepository.findByNourritureIdOrderByDateDesc(nourritureId);
    }

    public Optional<MvtStock> findById(Integer id) {
        return mvtStockRepository.findById(id);
    }

    public MvtStock save(MvtStock mvtStock) {
        MvtStock saved = mvtStockRepository.save(mvtStock);
        //si c'est une entrée de stock, créer un mouvement d'argent correspondant à l'achat
        if (saved.getType().getCode().equals("entree")) {
            creerMvtArgentPourAchat(saved);
        }
        //après chaque mouvement de stock, vérifier le seuil d'alerte
        verifierSeuilAlerte(saved.getNourriture().getId());
        return saved;
    }

    //créer un mouvement d'argent correspondant à l'achat de nourriture
    private void creerMvtArgentPourAchat(MvtStock mvtStock) {
        BigDecimal montant = mvtStock.getQuantite()
                .multiply(mvtStock.getNourriture().getPrixUnitaire());
        mvtArgentService.creerSortie(montant, mvtStock.getDate(), "achat_nourriture");
    }

    //verifier si le stock actuel est inférieur ou égal au seuil d'alerte de la nourriture
    private void verifierSeuilAlerte(Integer nourritureId) {
        Nourriture nourriture = nourritureService.findById(nourritureId)
                .orElse(null);
        if (nourriture == null || nourriture.getSeuilAlerte() == null) return;

        double stock = calculerStockActuel(nourritureId).doubleValue();
        if (stock <= nourriture.getSeuilAlerte()) {
            notificationService.creer("STOCK_FAIBLE");
        }
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

    //calculer le stock actuel d'une nourriture en soustrayant les sorties des entrées
    public BigDecimal calculerStockActuel(Integer nourritureId) {
        BigDecimal entrees = mvtStockRepository.sumQuantiteByNourritureAndType(nourritureId, getTypeEntree());
        BigDecimal sorties = mvtStockRepository.sumQuantiteByNourritureAndType(nourritureId, getTypeSortie());
        return entrees.subtract(sorties);
    }
}
