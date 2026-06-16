package com.app.eggland.entity;

import com.app.eggland.entity.StatutClient;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "client")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Client {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 100)
    private String prenom;

    @Column(length = 20)
    private String tel;

    @Column(length = 150)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut", nullable = false)
    private StatutClient statut;
}