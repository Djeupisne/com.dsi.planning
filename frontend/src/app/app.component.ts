import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <mat-toolbar color="primary">
      <span>TFJ Automation</span>
      <span class="toolbar-spacer"></span>
      <span>DSI - Planification des Travaux de Fin de Journée</span>
    </mat-toolbar>
    <app-planning-list></app-planning-list>
  `,
  styles: [`
    mat-toolbar {
      position: sticky;
      top: 0;
      z-index: 1000;
    }
    .toolbar-spacer {
      flex: 1 1 auto;
    }
  `]
})
export class AppComponent {
  title = 'TFJ Automation';
}
