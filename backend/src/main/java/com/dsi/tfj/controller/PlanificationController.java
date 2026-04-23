package com.dsi.tfj.controller;

import com.dsi.tfj.model.Planification;
import com.dsi.tfj.service.PlanificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/planning")
@CrossOrigin(origins = {"http://localhost:4200", "https://tfj-automation-frontend.onrender.com"})
public class PlanificationController {

    @Autowired
    private PlanificationService planificationService;

    @PostMapping("/generer-tfj/{annee}/{numeroSemaine}")
    public ResponseEntity<List<Planification>> genererPlanningTFJ(
            @PathVariable Integer annee,
            @PathVariable Integer numeroSemaine) {
        List<Planification> planning = planificationService.genererPlanningTFJ(annee, numeroSemaine);
        return ResponseEntity.ok(planning);
    }

    @PostMapping("/generer-permanence/{annee}/{numeroSemaine}")
    public ResponseEntity<List<Planification>> genererPlanningPermanence(
            @PathVariable Integer annee,
            @PathVariable Integer numeroSemaine) {
        List<Planification> planning = planificationService.genererPlanningPermanence(annee, numeroSemaine);
        return ResponseEntity.ok(planning);
    }

    @GetMapping("/semaine/{annee}/{numeroSemaine}")
    public ResponseEntity<List<Planification>> getPlanningSemaine(
            @PathVariable Integer annee,
            @PathVariable Integer numeroSemaine) {
        List<Planification> planning = planificationService.getPlanningSemaine(annee, numeroSemaine);
        return ResponseEntity.ok(planning);
    }

    @GetMapping("/periode")
    public ResponseEntity<List<Planification>> getPlanningPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        List<Planification> planning = planificationService.getPlanningPeriode(dateDebut, dateFin);
        return ResponseEntity.ok(planning);
    }

    @PostMapping("/reaffecter-absence/{absenceId}")
    public ResponseEntity<Void> reaffecterEnCasDAbsence(@PathVariable Long absenceId) {
        planificationService.reaffecterEnCasDAbsence(absenceId);
        return ResponseEntity.ok().build();
    }
}
