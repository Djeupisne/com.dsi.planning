package com.dsi.tfj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Entité représentant un jour férié
 */
@Entity
@Table(name = "jour_ferie")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JourFerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false)
    private String nom;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean jourChome = true;

    @Column(nullable = false)
    private LocalDate dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDate.now();
    }
}
