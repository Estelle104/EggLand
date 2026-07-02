package com.app.eggland.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "lot_races")
public class LotRace {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
@JoinColumn(name = "lot_id")
@JsonIgnore  
private Lot lot;
    
    @ManyToOne
    @JoinColumn(name = "race_id")
    private Race race;
    
    @Column(name = "nombre")
    private Integer nombre;
    
    public LotRace() {}
    
    public LotRace(Lot lot, Race race, Integer nombre) {
        this.lot = lot;
        this.race = race;
        this.nombre = nombre;
    }
    

    public Integer getId() { return id; }
    public Lot getLot() { return lot; }
    public void setLot(Lot lot) { this.lot = lot; }
    public Race getRace() { return race; }
    public void setRace(Race race) { this.race = race; }
    public Integer getNombre() { return nombre; }
    public void setNombre(Integer nombre) { this.nombre = nombre; }
}
