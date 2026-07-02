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
        System.out.println("Nombre de oeuf par jour : " + nbOeufParJour);
        System.out.println("Nombre de jours : " + nombreDeJours);
        System.out.println("Nombre de oeuf dans le futur : " + nbOeufAvenir);
        int chiffreAffaire = nbOeufAvenir * prixUnitaire;
        
        return chiffreAffaire;
    }
    public int calculDate(Date dateFin) {

        Date aujourdHui = new Date(System.currentTimeMillis());

        long diffMs = dateFin.getTime() - aujourdHui.getTime();

        long joursCalendaires = (diffMs / 86400000L) + 1;

        int joursTravail = (int) ((joursCalendaires * 22) / 30);

        return joursTravail+1;
    }

}
