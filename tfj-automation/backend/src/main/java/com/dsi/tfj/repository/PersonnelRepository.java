package com.dsi.tfj.repository;

import com.dsi.tfj.model.Groupe;
import com.dsi.tfj.model.Hierarchie;
import com.dsi.tfj.model.Personnel;
import com.dsi.tfj.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {
    
    Optional<Personnel> findByEmail(String email);
    
    List<Personnel> findByActifTrue();
    
    List<Personnel> findByGroupe(Groupe groupe);
    
    List<Personnel> findByGroupeAndActifTrue(Groupe groupe);
    
    List<Personnel> findByRole(Role role);
    
    List<Personnel> findByHierarchie(Hierarchie hierarchie);
    
    @Query("SELECT p FROM Personnel p LEFT JOIN FETCH p.groupe WHERE p.actif = true")
    List<Personnel> findAllActifsAvecGroupe();
    
    @Query("SELECT p FROM Personnel p LEFT JOIN FETCH p.groupe WHERE p.groupe = :groupe AND p.actif = true")
    List<Personnel> findByGroupeActif(@Param("groupe") Groupe groupe);
    
    @Query("SELECT COUNT(p) FROM Personnel p WHERE p.actif = true")
    long countPersonnelActif();
    
    @Query("SELECT COUNT(p) FROM Personnel p WHERE p.groupe = :groupe AND p.actif = true")
    long countByGroupe(@Param("groupe") Groupe groupe);
    
    @Query("SELECT p FROM Personnel p LEFT JOIN FETCH p.groupe WHERE p.hierarchie = :hierarchie AND p.actif = true")
    List<Personnel> findByHierarchieActif(@Param("hierarchie") Hierarchie hierarchie);
}
