# TFJ Automation - Planification des Travaux de Fin de Journée

[![Deploy to Render](https://img.shields.io/badge/Deploy_to-Render-46E3B7?style=for-the-badge&logo=render)](https://render.com)
[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-brightgreen?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-red?style=for-the-badge&logo=angular)](https://angular.io)

## 📖 Description
Application d'automatisation de la planification des travaux de fin de journée (TFJ) et des permanences pour la DSI.

## 🚀 Déploiement Rapide

### Sur Render.com (Recommandé)

Suivez le guide rapide: **[QUICKSTART_RENDER.md](QUICKSTART_RENDER.md)**

Ou le guide complet: **[DEPLOYMENT_RENDER.md](DEPLOYMENT_RENDER.md)**

```bash
# Script automatique de configuration
./setup-render.sh
```

### En local

Voir section [Installation](#installation) ci-dessous.

## Stack Technique
- **Backend**: Spring Boot 3.2, Java 17
- **Frontend**: Angular 17, TypeScript
- **Base de données**: PostgreSQL ou Oracle
- **Build**: Maven

## Fonctionnalités

### Règles de planification implémentées:
1. **TFJ (Lundi-Vendredi)**: Travaux de fin de journée
2. **Permanences (Samedi)**: Permanences week-end
3. **Rotation hebdomadaire**: Un membre affecté un jour prend le jour antérieur la semaine suivante
4. **Non-consécution**: Les membres d'un même groupe ne peuvent pas se suivre
5. **Membres isolés**: Les membres seuls dans leur groupe sont programmés uniquement les vendredis ou samedis
6. **Gestion des congés**: Prise en compte des absences
7. **Réaffectation automatique**: En cas d'absence exceptionnelle
8. **Managers**: Affectés seulement si nombre insuffisant de participants (seuil: 5)
9. **Jours fériés**: Gestion des jours fériés
10. **Demi-journées**: Support des demi-journées de congés

## Installation

### Prérequis
- Java 17+
- Node.js 18+
- PostgreSQL ou Oracle Database
- Maven 3.8+

### Backend

1. Configurer la base de données dans `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tfj_db
    username: postgres
    password: postgres
```

2. Démarrer l'application:
```bash
cd backend
mvn spring-boot:run
```

L'API sera disponible sur http://localhost:8080

### Frontend

1. Installer les dépendances:
```bash
cd frontend
npm install
```

2. Démarrer l'application:
```bash
npm start
```

L'application sera disponible sur http://localhost:4200

## API Endpoints

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/planning/generer-tfj/{annee}/{numeroSemaine}` | Générer planning TFJ |
| POST | `/api/planning/generer-permanence/{annee}/{numeroSemaine}` | Générer planning permanence |
| GET | `/api/planning/semaine/{annee}/{numeroSemaine}` | Récupérer planning semaine |
| GET | `/api/planning/periode?dateDebut=&dateFin=` | Récupérer planning période |
| POST | `/api/planning/reaffecter-absence/{absenceId}` | Réaffecter en cas d'absence |

## Structure du projet

```
tfj-automation/
├── backend/
│   ├── src/main/java/com/dsi/tfj/
│   │   ├── controller/      # Contrôleurs REST
│   │   ├── service/         # Services métier
│   │   ├── repository/      # Repositories JPA
│   │   ├── model/           # Entités JPA
│   │   └── dto/             # DTOs
│   └── src/main/resources/
│       └── application.yml  # Configuration
└── frontend/
    └── src/app/
        ├── components/      # Composants Angular
        ├── services/        # Services Angular
        └── models/          # Modèles TypeScript
```

## Modèle de données

### Entités principales:
- **Personnel**: Membres du personnel avec rôle et hiérarchie
- **Groupe**: Groupes par rôle/fiche de poste
- **Planification**: Affectations aux jours
- **Absence**: Congés et absences
- **JourFerie**: Jours fériés

### Enums:
- **Role**: ADMINISTRATEUR_RESEAU, DEVELOPPEUR, DBA, etc.
- **Hierarchie**: CADRE, MANAGER, DIRECTEUR
- **TypePlanification**: TFJ, PERMANENCE
- **TypeAbsence**: CONGES_PAYES, MALADIE, etc.

## License
Propriétaire - DSI
