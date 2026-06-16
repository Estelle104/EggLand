package com.app.eggland.entity;


import com.app.eggland.entity.StatutOeuf;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "oeufstatut")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class OeufStatut {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_id", nullable = false)
    private OeufProduction production;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut", nullable = false)
    private StatutOeuf statut;

    @Column(nullable = false)
    private Integer quantite;
}