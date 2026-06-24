package com.app.eggland.model;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "oeufproduction")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class OeufProduction {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private Lot lot;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer quantite;

    @OneToMany(mappedBy = "production", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OeufStatut> oeufStatuts = new ArrayList<>();

    public void ajouterStatut(OeufStatut oeufStatut) {
        if (this.oeufStatuts == null) {
            this.oeufStatuts = new ArrayList<>();
        }
        this.oeufStatuts.add(oeufStatut);
        oeufStatut.setProduction(this);
    }
}