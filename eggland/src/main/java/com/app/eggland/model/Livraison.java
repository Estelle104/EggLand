package com.app.eggland.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.app.eggland.model.StatutLivraison;

@Entity
@Table(name = "livraison")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Livraison {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vente_id", nullable = false)
    private Vente vente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "date_livraison", nullable = false)
    private LocalDate dateLivraison;

    @Column(name = "adresse_livraison", columnDefinition = "TEXT")
    private String adresseLivraison;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut", nullable = false)
    private StatutLivraison statut;

    @Column(name = "frais_livraison", nullable = false, precision = 12, scale = 2)
    private BigDecimal fraisLivraison;
}