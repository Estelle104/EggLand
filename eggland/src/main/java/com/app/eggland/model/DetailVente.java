package com.app.eggland.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

import com.app.eggland.model.ProduitVente;

@Entity
@Table(name = "detailvente")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class DetailVente {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vente_id", nullable = false)
    private Vente vente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produit", nullable = false)
    private ProduitVente produit;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantite;

    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    // Renseignés uniquement lorsque le produit vendu est "poule"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id")
    private Race race;
}