package com.dsi.tfj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Type de planification (TFJ ou Permanence)
 */
public enum TypePlanification {
    TFJ,          // Travaux de Fin de Journée (Lundi-Vendredi)
    PERMANENCE    // Permanence (Samedi)
}
