package com.app.eggland.entity;

import com.app.eggland.entity.RoleUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "useradmin")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAdmin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role", nullable = false)
    private RoleUser role;

    @Column(nullable = false)
    private Boolean actif;
}