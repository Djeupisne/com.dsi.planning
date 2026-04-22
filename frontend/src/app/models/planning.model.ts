export interface Personnel {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  role: Role;
  hierarchie: Hierarchie;
  groupe: Groupe;
  actif: boolean;
  dateEmbauche: string;
}

export interface Groupe {
  id: number;
  nom: string;
  description: string;
  role: Role;
  membres: Personnel[];
  actif: boolean;
}

export interface Planification {
  id: number;
  personnel: Personnel;
  date: string;
  type: TypePlanification;
  jourSemaine: DayOfWeek;
  numeroSemaine: number;
  annee: number;
  commentaires: string;
  dateCreation: string;
}

export interface Absence {
  id: number;
  personnel: Personnel;
  type: TypeAbsence;
  dateDebut: string;
  dateFin: string;
  demiJourneeMatin: boolean;
  demiJourneeApresMidi: boolean;
  commentaire: string;
  dateCreation: string;
}

export interface JourFerie {
  id: number;
  date: string;
  nom: string;
  description: string;
  jourChome: boolean;
  dateCreation: string;
}

export enum Role {
  ADMINISTRATEUR_RESEAU = 'ADMINISTRATEUR_RESEAU',
  DEVELOPPEUR = 'DEVELOPPEUR',
  ADMINISTRATEUR_SYSTEME = 'ADMINISTRATEUR_SYSTEME',
  DBA = 'DBA',
  CHEF_PROJET = 'CHEF_PROJET',
  EXPLOITANT = 'EXPLOITANT',
  AUTRE = 'AUTRE'
}

export enum Hierarchie {
  CADRE = 'CADRE',
  MANAGER = 'MANAGER',
  DIRECTEUR = 'DIRECTEUR'
}

export enum TypePlanification {
  TFJ = 'TFJ',
  PERMANENCE = 'PERMANENCE'
}

export enum TypeAbsence {
  CONGES_PAYES = 'CONGES_PAYES',
  CONGES_MALADIE = 'CONGES_MALADIE',
  CONGES_EXCEPTIONNELS = 'CONGES_EXCEPTIONNELS',
  RTT = 'RTT',
  FORMATION = 'FORMATION',
  AUTRE = 'AUTRE'
}

export enum DayOfWeek {
  MONDAY = 'MONDAY',
  TUESDAY = 'TUESDAY',
  WEDNESDAY = 'WEDNESDAY',
  THURSDAY = 'THURSDAY',
  FRIDAY = 'FRIDAY',
  SATURDAY = 'SATURDAY',
  SUNDAY = 'SUNDAY'
}

export interface PlanningRequest {
  annee: number;
  numeroSemaine: number;
}

export interface PlanningResponse {
  planning: Planification[];
  message?: string;
}
