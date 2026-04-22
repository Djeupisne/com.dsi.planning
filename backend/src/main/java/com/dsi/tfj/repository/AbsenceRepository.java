package com.dsi.tfj.repository;

import com.dsi.tfj.model.Absence;
import com.dsi.tfj.model.Personnel;
import com.dsi.tfj.model.TypeAbsence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    
    List<Absence> findByPersonnel(Personnel personnel);
    
    List<Absence> findByType(TypeAbsence type);
    
    @Query("SELECT a FROM Absence a LEFT JOIN FETCH a.personnel WHERE a.personnel = :personnel AND ((a.dateDebut <= :date AND a.dateFin >= :date))")
    List<Absence> findAbsencesByPersonnelAndDate(@Param("personnel") Personnel personnel, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Absence a LEFT JOIN FETCH a.personnel WHERE a.dateDebut <= :dateFin AND a.dateFin >= :dateDebut ORDER BY a.dateDebut")
    List<Absence> findAbsencesByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    @Query("SELECT a FROM Absence a LEFT JOIN FETCH a.personnel WHERE a.personnel = :personnel AND a.dateDebut <= :dateFin AND a.dateFin >= :dateDebut")
    List<Absence> findAbsencesByPersonnelAndPeriode(@Param("personnel") Personnel personnel, 
                                                     @Param("dateDebut") LocalDate dateDebut, 
                                                     @Param("dateFin") LocalDate dateFin);
    
    @Query("SELECT COUNT(a) FROM Absence a WHERE a.personnel = :personnel AND a.type = :type")
    long countByPersonnelAndType(@Param("personnel") Personnel personnel, @Param("type") TypeAbsence type);
}
