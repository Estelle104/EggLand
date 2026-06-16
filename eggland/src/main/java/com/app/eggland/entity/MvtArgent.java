package com.app.eggland.entity;

import com.app.eggland.entity.TypeMvt;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mvtargent")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class MvtArgent {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type", nullable = false)
    private TypeMvt type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 100)
    private String categorie;

    @Column(unique = true, length = 150)
    private String reference;
}