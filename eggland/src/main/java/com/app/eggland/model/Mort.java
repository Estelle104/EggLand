package com.app.eggland.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "mort")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Mort {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private Lot lot;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer nombre;
}