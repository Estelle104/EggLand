package com.app.eggland.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "typetraitement")
@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class TypeTraitement {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;
}