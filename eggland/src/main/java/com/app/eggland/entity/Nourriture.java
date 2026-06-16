package com.app.eggland.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "nourriture")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Nourriture {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(name = "prix_unitaire", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "seuil_alerte")
    private Integer seuilAlerte;
}