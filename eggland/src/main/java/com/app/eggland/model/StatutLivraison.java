package com.app.eggland.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "statutlivraison")
@Data @NoArgsConstructor @AllArgsConstructor
public class StatutLivraison {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;
}