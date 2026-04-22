package com.dsi.tfj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Type d'absence
 */
public enum TypeAbsence {
    CONGES_PAYES,
    CONGES_MALADIE,
    CONGES_EXCEPTIONNELS,
    RTT,
    FORMATION,
    AUTRE
}
