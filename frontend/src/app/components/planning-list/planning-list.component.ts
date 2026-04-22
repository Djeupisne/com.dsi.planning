import { Component, OnInit } from '@angular/core';
import { PlanningService } from '../../services/planning.service';
import { Planification, TypePlanification, DayOfWeek } from '../../models/planning.model';

@Component({
  selector: 'app-planning-list',
  template: `
    <div class="container">
      <h1>Planning des TFJ et Permanences</h1>
      
      <div class="card-container">
        <mat-card>
          <mat-card-header>
            <mat-card-title>Générer le planning</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <form (ngSubmit)="genererPlanning()">
              <mat-form-field appearance="outline">
                <mat-label>Semaine</mat-label>
                <input matInput type="number" [(ngModel)]="numeroSemaine" name="semaine" min="1" max="52" required>
              </mat-form-field>
              
              <mat-form-field appearance="outline">
                <mat-label>Année</mat-label>
                <input matInput type="number" [(ngModel)]="annee" name="annee" min="2024" max="2030" required>
              </mat-form-field>
              
              <div class="btn-group">
                <button mat-raised-button color="primary" type="submit">Générer TFJ</button>
                <button mat-raised-button color="accent" type="button" (click)="genererPermanence()">Générer Permanence</button>
                <button mat-raised-button color="warn" type="button" (click)="chargerPlanning()">Charger Planning</button>
              </div>
            </form>
          </mat-card-content>
        </mat-card>
      </div>

      <div *ngIf="message" [class]="'alert alert-' + messageType">{{ message }}</div>

      <mat-card *ngIf="planning && planning.length > 0">
        <mat-card-header>
          <mat-card-title>Planning de la semaine {{ numeroSemaine }}/{{ annee }}</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <table class="planning-table">
            <thead>
              <tr>
                <th>Date</th><th>Jour</th><th>Type</th><th>Nom</th><th>Prénom</th><th>Rôle</th><th>Groupe</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let p of planning" [style.background-color]="p.type === 'PERMANENCE' ? '#fff3e0' : ''">
                <td>{{ p.date | date:'dd/MM/yyyy' }}</td>
                <td>{{ getJourFr(p.jourSemaine) }}</td>
                <td><mat-chip [color]="p.type === 'TFJ' ? 'primary' : 'accent'">{{ p.type }}</mat-chip></td>
                <td>{{ p.personnel.nom }}</td>
                <td>{{ p.personnel.prenom }}</td>
                <td>{{ p.personnel.role }}</td>
                <td>{{ p.personnel.groupe.nom }}</td>
              </tr>
            </tbody>
          </table>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: []
})
export class PlanningListComponent implements OnInit {
  planning: Planification[] = [];
  numeroSemaine: number = 1;
  annee: number = new Date().getFullYear();
  message: string = '';
  messageType: 'success' | 'error' | 'info' = 'info';

  constructor(private planningService: PlanningService) {}

  ngOnInit(): void {
    const today = new Date();
    this.numeroSemaine = this.planningService.getNumeroSemaineISO(today);
    this.annee = this.planningService.getAnneeISO(today);
    this.chargerPlanning();
  }

  genererPlanning(): void {
    this.planningService.genererPlanningTFJ(this.annee, this.numeroSemaine).subscribe({
      next: (data) => {
        this.planning = data;
        this.message = 'Planning TFJ généré avec succès';
        this.messageType = 'success';
      },
      error: (err) => {
        this.message = 'Erreur: ' + err.message;
        this.messageType = 'error';
      }
    });
  }

  genererPermanence(): void {
    this.planningService.genererPlanningPermanence(this.annee, this.numeroSemaine).subscribe({
      next: (data) => {
        this.planning = [...this.planning, ...data];
        this.message = 'Planning Permanence généré';
        this.messageType = 'success';
      },
      error: (err) => {
        this.message = 'Erreur: ' + err.message;
        this.messageType = 'error';
      }
    });
  }

  chargerPlanning(): void {
    this.planningService.getPlanningSemaine(this.annee, this.numeroSemaine).subscribe({
      next: (data) => { this.planning = data; },
      error: (err) => {
        this.message = 'Erreur: ' + err.message;
        this.messageType = 'error';
      }
    });
  }

  getJourFr(jour: DayOfWeek): string {
    const jours: Record<string, string> = {
      'MONDAY': 'Lundi', 'TUESDAY': 'Mardi', 'WEDNESDAY': 'Mercredi',
      'THURSDAY': 'Jeudi', 'FRIDAY': 'Vendredi', 'SATURDAY': 'Samedi', 'SUNDAY': 'Dimanche'
    };
    return jours[jour] || jour;
  }
}
