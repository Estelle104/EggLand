package com.app.eggland.dto;

import java.math.BigDecimal;

public class SimulationMortaliteResult {
    private BigDecimal chiffreAffaire;
    private int nbOeufsRestants;
    private int totalMorts;
    private String nomRace;
    private String message;

    public SimulationMortaliteResult() {}

    public SimulationMortaliteResult(BigDecimal chiffreAffaire, int nbOeufsRestants, int totalMorts, String nomRace) {
        this.chiffreAffaire = chiffreAffaire;
        this.nbOeufsRestants = nbOeufsRestants;
        this.totalMorts = totalMorts;
        this.nomRace = nomRace;
    }

    // Getters et Setters
    public BigDecimal getChiffreAffaire() {
        return chiffreAffaire;
    }

    public void setChiffreAffaire(BigDecimal chiffreAffaire) {
        this.chiffreAffaire = chiffreAffaire;
    }

    public int getNbOeufsRestants() {
        return nbOeufsRestants;
    }

    public void setNbOeufsRestants(int nbOeufsRestants) {
        this.nbOeufsRestants = nbOeufsRestants;
    }

    public int getTotalMorts() {
        return totalMorts;
    }

    public void setTotalMorts(int totalMorts) {
        this.totalMorts = totalMorts;
    }

    public String getNomRace() {
        return nomRace;
    }

    public void setNomRace(String nomRace) {
        this.nomRace = nomRace;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}