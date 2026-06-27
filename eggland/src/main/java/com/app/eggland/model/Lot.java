package com.app.eggland.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.app.eggland.model.StatutLot;

@Entity
@Table(name = "lot")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Lot {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @Column(name = "date_arrivee", nullable = false)
    private LocalDate dateArrivee;

    @Column(name = "nombre_initial", nullable = false)
    private Integer nombreInitial;

    @Column(name = "age_semaine", nullable = false)
    private Integer ageSemaine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statut", nullable = false)
    private StatutLot statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batiment_id", nullable = false)
    private Batiment batiment;


  @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
private List<LotRace> lotRaces = new ArrayList<>();
    
    // Getters/Setters
    public List<LotRace> getLotRaces() { return lotRaces; }
    public void setLotRaces(List<LotRace> lotRaces) { this.lotRaces = lotRaces; }
}
