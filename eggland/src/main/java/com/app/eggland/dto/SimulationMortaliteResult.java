package com.app.eggland.dto;

import java.math.BigDecimal;
import java.util.Map;

public class SimulationMortaliteResult {
    private BigDecimal chiffreAffaire;
    private int nbOeufsRestants;
    private int totalMorts;
    private Map<String, Integer> mortsParRace;
    private String message;

    public SimulationMortaliteResult() {}

    public SimulationMortaliteResult(BigDecimal chiffreAffaire, int nbOeufsRestants, 
                                     int totalMorts, Map<String, Integer> mortsParRace) {
        this.chiffreAffaire = chiffreAffaire;
        this.nbOeufsRestants = nbOeufsRestants;
        this.totalMorts = totalMorts;
        this.mortsParRace = mortsParRace;
    }

    // Getters et Setters
    public BigDecimal getChiffreAffaire() { return chiffreAffaire; }
    public void setChiffreAffaire(BigDecimal chiffreAffaire) { this.chiffreAffaire = chiffreAffaire; }
    
    public int getNbOeufsRestants() { return nbOeufsRestants; }
    public void setNbOeufsRestants(int nbOeufsRestants) { this.nbOeufsRestants = nbOeufsRestants; }
    
    public int getTotalMorts() { return totalMorts; }
    public void setTotalMorts(int totalMorts) { this.totalMorts = totalMorts; }
    
    public Map<String, Integer> getMortsParRace() { return mortsParRace; }
    public void setMortsParRace(Map<String, Integer> mortsParRace) { this.mortsParRace = mortsParRace; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}