package com.app.eggland.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "versementsalaire")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersementSalaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "paiement_salaire_id", nullable = false)
    private PaiementSalaire paiementSalaire;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false)
    private LocalDate date;
}