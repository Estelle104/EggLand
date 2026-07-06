package com.app.eggland.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.Batiment;
import com.app.eggland.model.DetailVente;
import com.app.eggland.repository.DetailVenteRepository;

@Service
public class DetailVenteService {
    @Autowired
    private DetailVenteRepository detailVenteRepository;

    public void saveDetailVente(DetailVente detailVente) {
        detailVenteRepository.save(detailVente);
    }

    public List<DetailVente> listeDetailVente() {
        return detailVenteRepository.findAll();
    }

    public void modifierDetailVente(DetailVente detailVente) {
        detailVenteRepository.save(detailVente);
    }

    public void supprimerDetailVente(int id) {
        detailVenteRepository.deleteById(id);
    }
}
