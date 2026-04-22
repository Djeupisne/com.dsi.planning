package com.dsi.tfj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

/**
 * Groupe de personnel par rôle/fiche de poste
 */
@Entity
@Table(name = "groupe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Groupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "groupe", fetch = FetchType.LAZY)
    private Set<Personnel> membres = new HashSet<>();

    @Column(nullable = false)
    private boolean actif = true;
}
