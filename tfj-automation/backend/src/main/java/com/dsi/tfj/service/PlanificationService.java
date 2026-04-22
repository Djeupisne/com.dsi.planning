package com.dsi.tfj.service;

import com.dsi.tfj.model.*;
import com.dsi.tfj.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlanificationService {

    @Autowired
    private PersonnelRepository personnelRepository;
    
    @Autowired
    private GroupeRepository groupeRepository;
    
    @Autowired
    private AbsenceRepository absenceRepository;
    
    @Autowired
    private PlanificationRepository planificationRepository;
    
    @Autowired
    private JourFerieRepository jourFerieRepository;

    /**
     * Génère la planification des TFJ pour une semaine donnée
     */
    public List<Planification> genererPlanningTFJ(Integer annee, Integer numeroSemaine) {
        LocalDate lundi = getLundiDeLaSemaine(annee, numeroSemaine);
        LocalDate vendredi = lundi.plusDays(4);
        
        // Nettoyer l'ancienne planification de la semaine
        List<Planification> anciennes = planificationRepository.findBySemaine(numeroSemaine, annee);
        planificationRepository.deleteAll(anciennes);
        
        List<Planification> nouvellesPlanifications = new ArrayList<>();
        List<Groupe> groupes = groupeRepository.findAllActifsAvecMembres();
        
        // Jours ouvrés (Lundi à Vendredi)
        List<DayOfWeek> joursOuvres = Arrays.asList(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        );
        
        for (DayOfWeek jour : joursOuvres) {
            LocalDate date = getPremierJourOccurence(lundi, jour);
            
            // Vérifier si c'est un jour férié
            if (jourFerieRepository.findByDate(date).isPresent()) {
                continue;
            }
            
            List<Planification> planifJour = affecterPersonnelAuJour(date, jour, false);
            nouvellesPlanifications.addAll(planifJour);
        }
        
        return planificationRepository.saveAll(nouvellesPlanifications);
    }

    /**
     * Génère la planification des permanences pour les samedis
     */
    public List<Planification> genererPlanningPermanence(Integer annee, Integer numeroSemaine) {
        LocalDate lundi = getLundiDeLaSemaine(annee, numeroSemaine);
        LocalDate samedi = lundi.plusDays(5);
        
        // Nettoyer l'ancienne planification de la semaine
        List<Planification> anciennes = planificationRepository.findByTypeAndSemaine(TypePlanification.PERMANENCE, numeroSemaine, annee);
        planificationRepository.deleteAll(anciennes);
        
        List<Planification> planifSamedi = affecterPersonnelAuJour(samedi, DayOfWeek.SATURDAY, true);
        
        return planificationRepository.saveAll(planifSamedi);
    }

    /**
     * Affecte le personnel à un jour donné selon les règles métier
     */
    private List<Planification> affecterPersonnelAuJour(LocalDate date, DayOfWeek jour, boolean estPermanence) {
        List<Planification> planifications = new ArrayList<>();
        List<Groupe> groupes = groupeRepository.findAllActifsAvecMembres();
        
        // Récupérer les absences pour cette date
        List<Absence> absences = absenceRepository.findAbsencesByPeriode(date, date);
        Set<Long> idsPersonnelsAbsents = absences.stream()
            .map(a -> a.getPersonnel().getId())
            .collect(Collectors.toSet());
        
        // Vérifier les planifications précédentes pour appliquer la règle de rotation
        LocalDate semainePrecedente = date.minusWeeks(1);
        Integer anneePrecedente = semainePrecedente.getYear();
        Integer semainePrecedenteNum = semainePrecedente.get(WeekFields.ISO.weekOfWeekBasedYear());
        
        List<Planification> planifSemainePrecedente = planificationRepository.findBySemaine(semainePrecedenteNum, anneePrecedente);
        Map<Long, DayOfWeek> dernierJourParPersonnel = new HashMap<>();
        planifSemainePrecedente.forEach(p -> 
            dernierJourParPersonnel.put(p.getPersonnel().getId(), p.getJourSemaine())
        );
        
        for (Groupe groupe : groupes) {
            List<Personnel> membresEligibles = groupe.getMembres().stream()
                .filter(Personnel::isActif)
                .filter(p -> !idsPersonnelsAbsents.contains(p.getId()))
                .collect(Collectors.toList());
            
            if (membresEligibles.isEmpty()) {
                continue;
            }
            
            // Règle: membres seuls dans leur groupe -> uniquement Vendredi ou Samedi
            if (membresEligibles.size() == 1) {
                Personnel seulMembre = membresEligibles.get(0);
                if (jour == DayOfWeek.FRIDAY || (estPermanence && jour == DayOfWeek.SATURDAY)) {
                    Planification p = new Planification();
                    p.setPersonnel(seulMembre);
                    p.setDate(date);
                    p.setType(estPermanence ? TypePlanification.PERMANENCE : TypePlanification.TFJ);
                    p.setJourSemaine(jour);
                    p.setNumeroSemaine(date.get(WeekFields.ISO.weekOfWeekBasedYear()));
                    p.setAnnee(date.getYear());
                    planifications.add(p);
                }
                continue;
            }
            
            // Filtrer les managers si suffisamment de participants
            if (!estPermanence) {
                long nonManagers = membresEligibles.stream()
                    .filter(p -> p.getHierarchie() != Hierarchie.MANAGER)
                    .count();
                
                if (nonManagers >= 5) { // Seuil minimum
                    membresEligibles = membresEligibles.stream()
                        .filter(p -> p.getHierarchie() != Hierarchie.MANAGER)
                        .collect(Collectors.toList());
                }
            }
            
            // Appliquer la règle de rotation (jour antérieur la semaine suivante)
            Personnel personnelSelectionne = selectionnerPersonnelAvecRotation(
                membresEligibles, jour, dernierJourParPersonnel, date, absences
            );
            
            if (personnelSelectionne != null) {
                Planification p = new Planification();
                p.setPersonnel(personnelSelectionne);
                p.setDate(date);
                p.setType(estPermanence ? TypePlanification.PERMANENCE : TypePlanification.TFJ);
                p.setJourSemaine(jour);
                p.setNumeroSemaine(date.get(WeekFields.ISO.weekOfWeekBasedYear()));
                p.setAnnee(date.getYear());
                planifications.add(p);
            }
        }
        
        // Règle: membres d'un même groupe ne peuvent pas se suivre
        planifications = filtrerConsecutifsParGroupe(planifications);
        
        return planifications;
    }

    /**
     * Sélectionne le personnel en appliquant la règle de rotation
     */
    private Personnel selectionnerPersonnelAvecRotation(List<Personnel> membresEligibles, 
                                                         DayOfWeek jourActuel,
                                                         Map<Long, DayOfWeek> dernierJourParPersonnel,
                                                         LocalDate date,
                                                         List<Absence> absences) {
        // Trier les membres par ordre de priorité basé sur la rotation
        List<Personnel> tries = membresEligibles.stream()
            .sorted((p1, p2) -> {
                DayOfWeek jour1 = dernierJourParPersonnel.get(p1.getId());
                DayOfWeek jour2 = dernierJourParPersonnel.get(p2.getId());
                
                if (jour1 == null && jour2 == null) return 0;
                if (jour1 == null) return -1;
                if (jour2 == null) return 1;
                
                // Celui qui avait le jour le plus avancé la semaine précédente
                // devrait avoir le jour antérieur cette semaine
                return jour2.compareTo(jour1);
            })
            .collect(Collectors.toList());
        
        // Vérifier la disponibilité pour demi-journées
        for (Personnel p : tries) {
            boolean disponible = true;
            for (Absence a : absences) {
                if (a.getPersonnel().getId().equals(p.getId())) {
                    if (a.concerneDate(date)) {
                        disponible = false;
                        break;
                    }
                }
            }
            if (disponible) {
                return p;
            }
        }
        
        return null;
    }

    /**
     * Filtre les planifications pour éviter que des membres du même groupe se suivent
     */
    private List<Planification> filtrerConsecutifsParGroupe(List<Planification> planifications) {
        Map<Groupe, List<Planification>> parGroupe = planifications.stream()
            .collect(Collectors.groupingBy(p -> p.getPersonnel().getGroupe()));
        
        List<Planification> resultat = new ArrayList<>();
        
        for (Map.Entry<Groupe, List<Planification>> entry : parGroupe.entrySet()) {
            List<Planification> planifGroupe = entry.getValue().stream()
                .sorted(Comparator.comparing(Planification::getDate))
                .collect(Collectors.toList());
            
            Planification dernierePlanif = null;
            for (Planification p : planifGroupe) {
                if (dernierePlanif == null) {
                    resultat.add(p);
                    dernierePlanif = p;
                } else {
                    // Vérifier si les dates se suivent
                    if (!p.getDate().equals(dernierePlanif.getDate().plusDays(1))) {
                        resultat.add(p);
                        dernierePlanif = p;
                    }
                    // Si se suivent, on ne garde pas celle-ci (règle: ne peuvent pas se suivre)
                }
            }
        }
        
        return resultat;
    }

    /**
     * Réaffecte automatiquement en cas d'absence exceptionnelle
     */
    public void reaffecterEnCasDAbsence(Long absenceId) {
        Absence absence = absenceRepository.findById(absenceId)
            .orElseThrow(() -> new RuntimeException("Absence non trouvée"));
        
        LocalDate dateDebut = absence.getDateDebut();
        LocalDate dateFin = absence.getDateFin();
        Personnel personnelAbsent = absence.getPersonnel();
        
        // Pour chaque jour de l'absence
        for (LocalDate date = dateDebut; !date.isAfter(dateFin); date = date.plusDays(1)) {
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue; // Week-end
            }
            
            Optional<Planification> optPlanif = planificationRepository.findByPersonnelAndDate(personnelAbsent, date);
            if (optPlanif.isPresent()) {
                Planification planifAnnuler = optPlanif.get();
                
                // Trouver le personnel programmé le jour suivant
                LocalDate jourSuivant = date.plusDays(1);
                while (jourSuivant.getDayOfWeek() == DayOfWeek.SATURDAY || 
                       jourSuivant.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    jourSuivant = jourSuivant.plusDays(1);
                }
                
                // Annuler la planification originale
                planificationRepository.delete(planifAnnuler);
                
                // Créer une nouvelle planification pour le personnel du jour suivant
                List<Planification> planifJourSuivant = planificationRepository.findByDate(jourSuivant);
                for (Planification p : planifJourSuivant) {
                    if (!p.getPersonnel().getId().equals(personnelAbsent.getId())) {
                        // Réaffecter au jour initial
                        Planification nouvellePlanif = new Planification();
                        nouvellePlanif.setPersonnel(p.getPersonnel());
                        nouvellePlanif.setDate(date);
                        nouvellePlanif.setType(p.getType());
                        nouvellePlanif.setJourSemaine(date.getDayOfWeek());
                        nouvellePlanif.setNumeroSemaine(date.get(WeekFields.ISO.weekOfWeekBasedYear()));
                        nouvellePlanif.setAnnee(date.getYear());
                        nouvellePlanif.setCommentaires("Réaffectation automatique - absence de " + 
                            personnelAbsent.getNom() + " " + personnelAbsent.getPrenom());
                        planificationRepository.save(nouvellePlanif);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Obtient le lundi d'une semaine ISO donnée
     */
    private LocalDate getLundiDeLaSemaine(Integer annee, Integer numeroSemaine) {
        WeekFields weekFields = WeekFields.ISO;
        LocalDate premierJanvier = LocalDate.of(annee, 1, 1);
        int premiereSemaine = premierJanvier.get(weekFields.weekOfWeekBasedYear());
        
        // Trouver le lundi de la première semaine
        LocalDate lundiSemaine1 = premierJanvier;
        while (lundiSemaine1.get(weekFields.weekOfWeekBasedYear()) != premiereSemaine || 
               lundiSemaine1.getDayOfWeek() != DayOfWeek.MONDAY) {
            lundiSemaine1 = lundiSemaine1.plusDays(1);
        }
        
        return lundiSemaine1.plusWeeks(numeroSemaine - premiereSemaine);
    }

    /**
     * Obtient la première occurrence d'un jour de la semaine après une date donnée
     */
    private LocalDate getPremierJourOccurence(LocalDate date, DayOfWeek jour) {
        int daysToAdd = jour.getValue() - date.getDayOfWeek().getValue();
        if (daysToAdd < 0) {
            daysToAdd += 7;
        }
        return date.plusDays(daysToAdd);
    }

    /**
     * Vérifie si un personnel est disponible à une date donnée
     */
    public boolean isPersonnelDisponible(Long personnelId, LocalDate date) {
        Personnel personnel = personnelRepository.findById(personnelId)
            .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));
        
        List<Absence> absences = absenceRepository.findAbsencesByPersonnelAndDate(personnel, date);
        return absences.isEmpty();
    }

    /**
     * Obtient le planning complet pour une période
     */
    @Transactional(readOnly = true)
    public List<Planification> getPlanningPeriode(LocalDate dateDebut, LocalDate dateFin) {
        return planificationRepository.findByPeriode(dateDebut, dateFin);
    }

    /**
     * Obtient le planning pour une semaine spécifique
     */
    @Transactional(readOnly = true)
    public List<Planification> getPlanningSemaine(Integer annee, Integer numeroSemaine) {
        return planificationRepository.findBySemaine(numeroSemaine, annee);
    }
}
