package com.app.eggland.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal salaire;

    @Column(name = "date_embauche", nullable = false)
    private LocalDate dateEmbauche;
}