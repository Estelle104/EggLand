package com.app.eggland.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.Vente;
import com.app.eggland.repository.VenteRepository;

@Service
public class VenteService {
    @Autowired
    private VenteRepository venteRepository;

    public void saveVente(Vente vente) {
        venteRepository.save(vente);
    }

    public List<Vente> listeVente() {
        return venteRepository.findAll();
    }

    public void supprimerVente(int id) {
        venteRepository.deleteById(id);
    }

    public Vente trouverVenteParId(int id) {
        return venteRepository.findById(id).orElse(null);
    }

    
}
