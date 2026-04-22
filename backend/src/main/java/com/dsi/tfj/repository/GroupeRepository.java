package com.dsi.tfj.repository;

import com.dsi.tfj.model.Groupe;
import com.dsi.tfj.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    
    Optional<Groupe> findByNom(String nom);
    
    List<Groupe> findByActifTrue();
    
    List<Groupe> findByRole(Role role);
    
    @Query("SELECT g FROM Groupe g LEFT JOIN FETCH g.membres WHERE g.actif = true")
    List<Groupe> findAllActifsAvecMembres();
    
    @Query("SELECT COUNT(g) FROM Groupe g WHERE g.actif = true")
    long countGroupesActifs();
}
