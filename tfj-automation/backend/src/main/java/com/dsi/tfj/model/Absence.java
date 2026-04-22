package com.dsi.tfj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entité représentant une absence d'un membre du personnel
 */
@Entity
@Table(name = "absence")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAbsence type;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Column(nullable = false)
    private Boolean demiJourneeMatin = false;

    @Column(nullable = false)
    private Boolean demiJourneeApresMidi = false;

    @Column(length = 500)
    private String commentaire;

    @Column(nullable = false)
    private LocalDate dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDate.now();
    }

    /**
     * Vérifie si l'absence concerne une date spécifique
     */
    public boolean concerneDate(LocalDate date) {
        if (date.isBefore(dateDebut) || date.isAfter(dateFin)) {
            return false;
        }
        
        // Si c'est une demi-journée, vérifier si c'est le premier ou dernier jour
        if (demiJourneeMatin || demiJourneeApresMidi) {
            if (!date.equals(dateDebut) && !date.equals(dateFin)) {
                return true; // Jour complet entre les deux
            }
            // Premier ou dernier jour avec demi-journée
            return true;
        }
        
        return true;
    }

    /**
     * Vérifie si l'absence concerne un jour spécifique (matin ou après-midi)
     */
    public boolean concerneDemiJournee(LocalDate date, boolean matin) {
        if (!concerneDate(date)) {
            return false;
        }
        
        // Si c'est une absence complète
        if (!demiJourneeMatin && !demiJourneeApresMidi) {
            return true;
        }
        
        // Si c'est le premier jour et demi-journée matin
        if (date.equals(dateDebut) && demiJourneeMatin && matin) {
            return true;
        }
        
        // Si c'est le dernier jour et demi-journée après-midi
        if (date.equals(dateFin) && demiJourneeApresMidi && !matin) {
            return true;
        }
        
        // Jours entre les deux (si période multi-jours)
        if (!date.equals(dateDebut) && !date.equals(dateFin)) {
            return true;
        }
        
        return false;
    }
}
