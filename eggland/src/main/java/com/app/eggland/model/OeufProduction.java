package com.app.eggland.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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
}