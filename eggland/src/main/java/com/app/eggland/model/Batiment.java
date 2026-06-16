package com.app.eggland.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "batiment")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Batiment {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false)
    private Integer capacite;
}