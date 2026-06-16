package com.app.eggland.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "configuration")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Configuration {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "seuil_mort", nullable = false)
    private Integer seuilMort;

    @Column(name = "seuil_nourriture", nullable = false)
    private Double seuilNourriture;
}