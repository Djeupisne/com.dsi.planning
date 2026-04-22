package com.dsi.tfj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Entité représentant une planification de TFJ ou de permanence
 */
@Entity
@Table(name = "planification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Planification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePlanification type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek jourSemaine;

    @Column(nullable = false)
    private Integer numeroSemaine;

    @Column(nullable = false)
    private Integer annee;

    @Column(length = 500)
    private String commentaires;

    @Column(nullable = false)
    private LocalDate dateCreation;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDate.now();
    }

    /**
     * Vérifie si la planification peut être modifiée
     */
    public boolean isModifiable() {
        return dateCreation.isBefore(LocalDate.now().minusDays(1));
    }
}
