package com.app.eggland.service;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SimulationService {
    @Autowired
    private OeufService oeufService;

    public int runSimulation(Date dateFin,int nombreOeufs,int prixUnitaire){
        int nombreDeJours = calculDate(dateFin);

        int nbOeufActuelle = oeufService.getStockDisponible();

        System.out.println("Nombre de oeuf actuelle : " + nbOeufActuelle);
        int nbOeufParJour = nombreDeJours * nombreOeufs;
        int nbOeufAvenir = nbOeufActuelle + nbOeufParJour;

        int chiffreAffaire = nbOeufAvenir * prixUnitaire;
        
        return chiffreAffaire;
    }

    public int calculDate(Date dateFin){
        int nombreDeJours = (int) ((dateFin.getTime() - new Date(System.currentTimeMillis()).getTime()) / (1000 * 60 * 60 * 24));
        return nombreDeJours+1;
    }

}
