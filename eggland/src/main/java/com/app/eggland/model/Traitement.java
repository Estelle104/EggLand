package com.app.eggland.model;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.app.eggland.model.TypeTraitement;

@Entity
@Table(name = "traitement")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Traitement {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private Lot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type", nullable = false)
    private TypeTraitement type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cout;
}