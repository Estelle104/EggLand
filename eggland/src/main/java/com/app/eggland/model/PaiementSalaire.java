package com.app.eggland.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "paiementsalaire")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaiementSalaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    /** Toujours le 1er jour du mois concerné, ex: 2026-06-01 */
    @Column(nullable = false)
    private LocalDate mois;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false)
    @Builder.Default
    private Boolean paye = false;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @Column(length = 150, unique = true)
    private String reference;
}
