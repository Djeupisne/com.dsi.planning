package com.dsi.tfj.repository;

import com.dsi.tfj.model.DayOfWeek;
import com.dsi.tfj.model.Personnel;
import com.dsi.tfj.model.Planification;
import com.dsi.tfj.model.TypePlanification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanificationRepository extends JpaRepository<Planification, Long> {
    
    List<Planification> findByPersonnel(Personnel personnel);
    
    List<Planification> findByType(TypePlanification type);
    
    List<Planification> findByJourSemaine(DayOfWeek jour);
    
    @Query("SELECT p FROM Planification p LEFT JOIN FETCH p.personnel WHERE p.date = :date")
    List<Planification> findByDate(@Param("date") LocalDate date);
    
    @Query("SELECT p FROM Planification p LEFT JOIN FETCH p.personnel WHERE p.numeroSemaine = :numeroSemaine AND p.annee = :annee ORDER BY p.date")
    List<Planification> findBySemaine(@Param("numeroSemaine") Integer numeroSemaine, @Param("annee") Integer annee);
    
    @Query("SELECT p FROM Planification p LEFT JOIN FETCH p.personnel WHERE p.date BETWEEN :dateDebut AND :dateFin ORDER BY p.date")
    List<Planification> findByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    @Query("SELECT p FROM Planification p LEFT JOIN FETCH p.personnel WHERE p.personnel = :personnel AND p.numeroSemaine = :numeroSemaine AND p.annee = :annee")
    List<Planification> findByPersonnelAndSemaine(@Param("personnel") Personnel personnel, 
                                                   @Param("numeroSemaine") Integer numeroSemaine, 
                                                   @Param("annee") Integer annee);
    
    @Query("SELECT p FROM Planification p LEFT JOIN FETCH p.personnel WHERE p.type = :type AND p.numeroSemaine = :numeroSemaine AND p.annee = :annee")
    List<Planification> findByTypeAndSemaine(@Param("type") TypePlanification type, 
                                              @Param("numeroSemaine") Integer numeroSemaine, 
                                              @Param("annee") Integer annee);
    
    @Query("SELECT p FROM Planification p LEFT JOIN FETCH p.personnel WHERE p.personnel = :personnel AND p.date >= :date ORDER BY p.date")
    List<Planification> findByPersonnelAndDateAfter(@Param("personnel") Personnel personnel, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(p) FROM Planification p WHERE p.personnel = :personnel AND p.type = :type")
    long countByPersonnelAndType(@Param("personnel") Personnel personnel, @Param("type") TypePlanification type);
    
    Optional<Planification> findByPersonnelAndDate(Personnel personnel, LocalDate date);
}
