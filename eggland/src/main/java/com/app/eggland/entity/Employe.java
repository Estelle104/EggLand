package com.app.eggland.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "employe")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Employe {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(length = 20)
    private String tel;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salaire;
}