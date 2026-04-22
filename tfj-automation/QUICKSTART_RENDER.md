# 🚀 Déploiement Rapide sur Render.com

## ⚡ Guide Express (5 minutes)

### 1️⃣ Push vers GitHub

```bash
cd /workspace/tfj-automation

# Initialiser Git si ce n'est pas fait
git init
git add .
git commit -m "Initial commit - TFJ Automation"
git branch -M main

# Remplacez VOTRE_USERNAME par votre pseudo GitHub
git remote add origin https://github.com/VOTRE_USERNAME/tfj-automation.git
git push -u origin main
```

### 2️⃣ Créer la base de données PostgreSQL

1. Allez sur https://dashboard.render.com/
2. Cliquez **New** → **PostgreSQL**
3. Remplissez:
   - **Name**: `tfj-db`
   - **Database**: `tfj_db`
   - **User**: `tfj_user`
4. Choisissez le plan **Free**
5. **Créez** et **copiez** les informations de connexion

### 3️⃣ Créer le Web Service

1. Cliquez **New** → **Web Service**
2. Connectez votre repo: `tfj-automation`
3. Configurez:

| Champ | Valeur |
|-------|--------|
| **Name** | `tfj-automation` |
| **Region** | Même que la BDD |
| **Branch** | `main` |
| **Build Command** | `cd backend && mvn clean package -DskipTests` |
| **Start Command** | `java -jar backend/target/tfj-automation-1.0.0.jar --spring.profiles.active=render` |
| **Instance Type** | Free |

### 4️⃣ Variables d'environnement

Dans l'onglet **Environment**, ajoutez:

```
DATABASE_URL=jdbc:postgresql://ep-xxx-xxx.us-east-2.aws.neon.tech/tfj_db
DATABASE_USER=tfj_user
DATABASE_PASSWORD=votre_mot_de_passe_render
JWT_SECRET=ma-cle-secrete-tres-longue-et-aleatoire-123456789
PORT=8080
SPRING_PROFILES_ACTIVE=render
```

> ⚠️ **Important**: Récupérez `DATABASE_URL` depuis la page de votre base de données Render (section "Connection Info")

### 5️⃣ Déployer !

1. Cliquez **Create Web Service**
2. Attendez le build (~2-3 minutes)
3. Une fois vert ✅, votre API est en ligne !

### 6️⃣ Tester

```bash
# Remplacez par votre URL Render
export RENDER_URL=https://tfj-automation.onrender.com

# Health check
curl $RENDER_URL/api/actuator/health

# Liste des planifications
curl $RENDER_URL/api/planning

# Générer une planification
curl -X POST $RENDER_URL/api/planning/generate \
  -H "Content-Type: application/json" \
  -d '{"startDate": "2024-01-01", "endDate": "2024-01-31"}'
```

---

## 📋 Checklist de vérification

- [ ] Code pushé sur GitHub
- [ ] Base de données PostgreSQL créée sur Render
- [ ] Web Service créé avec les bonnes commandes
- [ ] Variables d'environnement configurées
- [ ] Health check retourne `{"status":"UP"}`
- [ ] Endpoints API fonctionnels

---

## 🔧 En cas de problème

### Le service ne démarre pas
```bash
# Vérifiez les logs dans le dashboard Render
# Onglet "Logs" de votre Web Service
```

### Erreur de connexion BDD
- Vérifiez que `DATABASE_URL`, `USER`, et `PASSWORD` sont corrects
- Assurez-vous que la BDD et le service sont dans la même région

### Timeout au démarrage
- C'est normal sur le plan Free (premier démarrage lent)
- Attendez 3-4 minutes maximum

---

## 💰 Coûts

| Service | Plan | Prix/mois |
|---------|------|-----------|
| PostgreSQL | Free | $0 |
| Web Service | Free | $0 (s'endort) |
| Web Service | Starter | $7 (toujours actif) |

**Total**: Gratuit pour tester, ~$14/mois pour production

---

## 📚 Pour aller plus loin

Consultez `DEPLOYMENT_RENDER.md` pour le guide complet avec:
- Configuration avancée
- Health checks
- Auto-deploy
- Sécurité
- Monitoring

---

**Support**: 
- Docs Render: https://render.com/docs
- Issues: GitHub repository
