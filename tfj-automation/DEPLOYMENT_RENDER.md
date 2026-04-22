# Guide de Déploiement sur Render.com

## Prérequis
- Compte GitHub avec le projet pushé
- Compte Render.com créé

## Étapes de déploiement

### 1. Pusher le code sur GitHub

```bash
cd /workspace/tfj-automation
git init
git add .
git commit -m "Initial commit - TFJ Automation"
git branch -M main
git remote add origin https://github.com/VOTRE_USERNAME/tfj-automation.git
git push -u origin main
```

### 2. Créer la base de données PostgreSQL sur Render

1. Connectez-vous à [Render Dashboard](https://dashboard.render.com/)
2. Cliquez sur **New** → **PostgreSQL**
3. Configurez:
   - **Name**: `tfj-db`
   - **Database**: `tfj_db`
   - **User**: `tfj_user`
   - **Password**: (généré automatiquement, sauvegardez-le)
   - **Region**: Choisissez la plus proche de vos utilisateurs
   - **Plan**: Free (pour tester) ou Starter

4. Notez les informations de connexion:
   - Host
   - Port (généralement 5432)
   - Database name
   - User
   - Password

### 3. Créer le service Web sur Render

1. Dans le Dashboard Render, cliquez sur **New** → **Web Service**
2. Connectez votre repository GitHub: `tfj-automation`
3. Configurez le service:

#### Configuration de base
- **Name**: `tfj-automation`
- **Region**: Même région que la base de données
- **Branch**: `main`
- **Root Directory**: Laissez vide
- **Runtime**: `Docker` ou `Java`

#### Build Settings
- **Build Command**: 
  ```bash
  cd backend && mvn clean package -DskipTests
  ```
- **Start Command**: 
  ```bash
  java -jar backend/target/tfj-automation-1.0.0.jar --spring.profiles.active=render
  ```

#### Environment Variables (CRUCIAL)
Ajoutez les variables d'environnement suivantes:

| Variable | Valeur | Description |
|----------|--------|-------------|
| `DATABASE_URL` | `jdbc:postgresql://<host>:<port>/tfj_db` | URL complète de la BDD |
| `DATABASE_USER` | `tfj_user` | Utilisateur BDD |
| `DATABASE_PASSWORD` | `<votre-mot-de-passe>` | Mot de passe BDD |
| `JWT_SECRET` | `une-cle-secrete-tres-longue-et-aleatoire-minimum-256-bits` | Clé JWT sécurisée |
| `PORT` | `8080` | Port du serveur (automatique sur Render) |
| `SPRING_PROFILES_ACTIVE` | `render` | Profile Spring Boot |

**Note**: Pour `DATABASE_URL`, utilisez la valeur fournie par Render dans la section "Connection Info" de votre base de données PostgreSQL.

### 4. Configuration avancée (optionnelle)

#### Health Checks
Dans l'onglet **Settings** du service Web:
- **Health Check Path**: `/api/actuator/health`
- Render vérifiera automatiquement la santé de l'application

#### Auto-Deploy
- Assurez-vous que **Auto-Deploy** est activé
- Chaque push sur la branche `main` redéploiera automatiquement l'application

### 5. Vérification du déploiement

1. Après le déploiement, Render fournit une URL: `https://tfj-automation.onrender.com`
2. Testez l'API:
   ```bash
   curl https://tfj-automation.onrender.com/api/actuator/health
   ```
3. Testez les endpoints:
   ```bash
   # Liste des planifications
   curl https://tfj-automation.onrender.com/api/planning
   
   # Générer une planification
   curl -X POST https://tfj-automation.onrender.com/api/planning/generate \
     -H "Content-Type: application/json" \
     -d '{"startDate": "2024-01-01", "endDate": "2024-01-31"}'
   ```

## Structure des fichiers pour Render

```
tfj-automation/
├── backend/
│   ├── pom.xml
│   ├── mvnw
│   └── src/main/resources/
│       ├── application.yml (développement local)
│       └── application-render.yml (production Render)
├── frontend/
│   └── (fichiers Angular)
├── render.yaml
└── README.md
```

## Variables d'environnement requises

Le fichier `application-render.yml` utilise les variables suivantes:

- `DATABASE_URL`: URL JDBC complète de PostgreSQL
- `DATABASE_USER`: Utilisateur de la base de données
- `DATABASE_PASSWORD`: Mot de passe de la base de données
- `JWT_SECRET`: Clé secrète pour signer les tokens JWT
- `PORT`: Port d'écoute (par défaut 8080)

## Commandes utiles

### Voir les logs sur Render
```bash
# Via le dashboard Render: Logs tab
# Ou via CLI Render (si installée)
render logs -s tfj-automation
```

### Redémarrer le service
```bash
# Via le dashboard Render: Manual Deploy button
```

### Mettre à jour après un changement de code
```bash
git push origin main
# Le déploiement automatique se déclenchera
```

## Résolution de problèmes

### L'application ne démarre pas
1. Vérifiez les logs dans le dashboard Render
2. Assurez-vous que toutes les variables d'environnement sont définies
3. Vérifiez que la base de données est accessible

### Erreur de connexion à la base de données
1. Vérifiez que `DATABASE_URL`, `DATABASE_USER`, et `DATABASE_PASSWORD` sont corrects
2. Assurez-vous que la base de données est dans la même région que le service Web
3. Vérifiez les règles de firewall de la base de données

### Timeout au démarrage
- Render a un timeout de démarrage de 3 minutes
- Optimisez le temps de démarrage en réduisant les logs et en utilisant un pool de connexions approprié

## Coûts estimés

- **PostgreSQL Free**: Gratuit (avec limitations)
- **Web Service Free**: Gratuit (s'endort après 15 min d'inactivité)
- **Web Service Starter**: ~$7/mois (toujours actif)

Pour une application de production, prévoyez environ **$15-20/mois** pour avoir:
- PostgreSQL Starter ($7/mois)
- Web Service Starter ($7/mois)
- Bandwidth et stockage supplémentaires si nécessaire

## Sécurité

1. **Changez la JWT_SECRET** avec une valeur aléatoire forte
2. **Utilisez HTTPS** (automatique sur Render)
3. **Limitez l'accès à la base de données** (Render le fait par défaut)
4. **Activez les backups** de la base de données
5. **Revoyez les logs** régulièrement pour détecter les anomalies

## Support

- Documentation Render: https://render.com/docs
- Spring Boot sur Render: https://render.com/docs/deploy-spring-boot
- Issues GitHub: Créez une issue sur votre repository
