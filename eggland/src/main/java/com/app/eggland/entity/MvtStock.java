package com.app.eggland.entity;


import com.app.eggland.entity.TypeMvt;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mvtstock")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class MvtStock {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nourriture_id", nullable = false)
    private Nourriture nourriture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")  
    private Lot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type", nullable = false)
    private TypeMvt type;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantite;

    @Column(nullable = false)
    private LocalDate date;
}