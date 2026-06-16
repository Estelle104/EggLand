package com.app.eggland.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roleuser")
@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class RoleUser {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;
}