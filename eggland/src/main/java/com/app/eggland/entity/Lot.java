package com.app.eggland.entity;


import com.app.eggland.entity.StatutLot;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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
}