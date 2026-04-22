package com.dsi.tfj.repository;

import com.dsi.tfj.model.JourFerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JourFerieRepository extends JpaRepository<JourFerie, Long> {
    
    Optional<JourFerie> findByDate(LocalDate date);
    
    @Query("SELECT j FROM JourFerie j WHERE j.date BETWEEN :dateDebut AND :dateFin ORDER BY j.date")
    List<JourFerie> findByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    @Query("SELECT COUNT(j) FROM JourFerie j WHERE j.date BETWEEN :dateDebut AND :dateFin AND j.jourChome = true")
    long countJoursFeriesPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    @Query("SELECT j FROM JourFerie j WHERE j.jourChome = true ORDER BY j.date")
    List<JourFerie> findAllJoursChomes();
}
