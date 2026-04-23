import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Planification, PlanningRequest, Absence, JourFerie } from '../models/planning.model';

@Injectable({
  providedIn: 'root'
})
export class PlanningService {
  
  private apiUrl = '/api';

  constructor(private http: HttpClient) { }

  genererPlanningTFJ(annee: number, numeroSemaine: number): Observable<Planification[]> {
    return this.http.post<Planification[]>(`${this.apiUrl}/planning/generer-tfj/${annee}/${numeroSemaine}`, {});
  }

  genererPlanningPermanence(annee: number, numeroSemaine: number): Observable<Planification[]> {
    return this.http.post<Planification[]>(`${this.apiUrl}/planning/generer-permanence/${annee}/${numeroSemaine}`, {});
  }

  getPlanningSemaine(annee: number, numeroSemaine: number): Observable<Planification[]> {
    return this.http.get<Planification[]>(`${this.apiUrl}/planning/semaine/${annee}/${numeroSemaine}`);
  }

  getPlanningPeriode(dateDebut: Date, dateFin: Date): Observable<Planification[]> {
    let params = new HttpParams()
      .set('dateDebut', dateDebut.toISOString().split('T')[0])
      .set('dateFin', dateFin.toISOString().split('T')[0]);
    
    return this.http.get<Planification[]>(`${this.apiUrl}/planning/periode`, { params });
  }

  reaffecterEnCasDAbsence(absenceId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/planning/reaffecter-absence/${absenceId}`, {});
  }

  // Helper pour obtenir le numéro de semaine ISO
  getNumeroSemaineISO(date: Date): number {
    const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    const dayNum = d.getUTCDay() || 7;
    d.setUTCDate(d.getUTCDate() + 4 - dayNum);
    const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
    return Math.ceil((((d.getTime() - yearStart.getTime()) / 86400000) + 1) / 7);
  }

  // Helper pour obtenir l'année ISO
  getAnneeISO(date: Date): number {
    const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    const dayNum = d.getUTCDay() || 7;
    d.setUTCDate(d.getUTCDate() + 4 - dayNum);
    return d.getUTCFullYear();
  }
}
