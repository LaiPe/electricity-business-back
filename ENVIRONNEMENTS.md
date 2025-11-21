# Guide des Environnements d'Exécution - Electricity Business

Ce projet Spring Boot supporte trois environnements d'exécution distincts avec bases de données hybrides (MySQL + MongoDB) :

## 1. 🛠️ Environnement de Développement (dev)

**Bases de données :** H2 en mémoire + MongoDB local  
**Exécution :** Locale avec Spring Boot DevTools

### Lancement
```bash
# Via Maven avec profil
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# (Nécessite des guillements sur PowerShell)
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"

# Via IntelliJ/Eclipse
# Définir VM options: -Dspring-boot.run.profiles=dev
```

### Caractéristiques
- **MySQL** : Remplacé par H2 en mémoire (se remet à zéro à chaque redémarrage)
- **MongoDB** : Local sur localhost:27017
- Console H2 accessible : http://localhost:8080/h2-console
    - JDBC URL: `jdbc:h2:mem:testdb`
    - User: `sa`
    - Password: (vide)
- Logs détaillés pour le debug (SQL + MongoDB)
- DDL auto: `create-drop`
- CORS activé pour localhost:3000 et localhost:5173
- Spring DevTools activé

---

## 2. 🧪 Environnement de Pré-production (preprod)

**Bases de données :** MySQL 8.3 + MongoDB 7.0 via conteneurs Docker  
**Exécution :** Application buildée en conteneur Docker

### Lancement
```bash
# Construire et lancer via Docker Compose
docker-compose up --build

# En arrière-plan
docker-compose up -d --build

# Arrêter
docker-compose down
```

### Caractéristiques
- **MySQL 8.3** dans un conteneur Docker (port 3306 exposé)
- **MongoDB 7.0** dans un conteneur Docker (port 27018 exposé localement)
- Application buildée dans un conteneur Docker
- Configuration via variables d'environnement
- Bases de données persistantes (volumes Docker)
- DDL auto: `update`
- Health checks configurés pour tous les services
- Logs détaillés maintenus pour debug

---

## 3. 🚀 Environnement de Production (prod)

**Bases de données :** MySQL 8.3 + MongoDB 7.0 via conteneurs Docker sécurisés  
**Exécution :** Image Docker depuis GitHub Container Registry

### Prérequis
1. Créer le fichier `.env.prod` avec les configurations sécurisées
2. Personnaliser tous les mots de passe et clés secrètes

### Lancement
```bash
# Lancer avec Docker Compose
docker-compose --env-file .env.prod -f docker-compose.prod.yml up -d

# Arrêter
docker-compose --env-file .env.prod -f docker-compose.prod.yml down
```

### Caractéristiques
- **MySQL 8.3** optimisé pour la production avec sécurité renforcée
- **MongoDB 7.0** avec authentification
- Image de l'application depuis `ghcr.io/laipe/laipe/electricity-business-back:latest`
- Configuration sécurisée via fichier `.env.prod`
- DDL auto: `update`
- Logs minimaux en production
- Health checks configurés
- Connection pool Hikari optimisé (20 connexions max, 5 minimum)
- Conteneurs sécurisés (read-only, no-new-privileges, capabilities restreintes)
- Réseau isolé pour la sécurité

---

## 📋 Contenu de `.env.prod`

| Variable | Description |
|----------|-------------|
| `MYSQL_ROOT_PASSWORD` | Mot de passe administrateur MySQL |
| `MYSQL_DATABASE` | Nom de la base de données MySQL (`eb_db`) |
| `MYSQL_USER` | Utilisateur MySQL pour l'application |
| `MYSQL_PASSWORD` | Mot de passe de l'utilisateur MySQL |
| `JDBC_HOST` | Nom du conteneur MySQL (`mysql`) |
| `DB_USER` | Utilisateur MySQL pour Spring Boot |
| `DB_PASS` | Mot de passe MySQL pour Spring Boot |
| `MONGO_HOST` | Nom du conteneur MongoDB (`mongodb`) |
| `MONGO_PORT` | Port de connexion MongoDB (27017) |
| `MONGO_USER` | Utilisateur MongoDB pour l'application |
| `MONGO_PASS` | Mot de passe MongoDB |
| `MONGO_INITDB_ROOT_USERNAME` | Utilisateur administrateur MongoDB |
| `MONGO_INITDB_ROOT_PASSWORD` | Mot de passe administrateur MongoDB |
| `MONGO_INITDB_DATABASE` | Base de données MongoDB initiale (`eb_db`) |
| `JWT_SECRET` | Clé secrète pour signer les tokens JWT |
| `CORS_ALLOWED_ORIGINS` | Origines CORS autorisées (séparées par virgules) |

### 🔐 Génération de mots de passe sécurisés :

```bash
# Générer une clé JWT sécurisée (32 caractères base64)
openssl rand -base64 32

# Ou avec PowerShell
[System.Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

## ⚠️ Notes importantes

1. **Architecture hybride** : Le projet utilise MySQL pour les données relationnelles et MongoDB pour les documents
2. **Sécurité** : Ne jamais commiter le fichier `.env.prod` avec des informations sensibles
3. **Données** : L'environnement dev (H2) remet à zéro les données à chaque redémarrage
4. **Performance** : L'environnement prod utilise HikariCP optimisé (pool de 20 connexions)
5. **Logs** : Verbeux en dev/preprod, minimaux en prod
6. **CORS** : Configuré pour les frontend localhost:3000 et localhost:5173
7. **Monitoring** : Actuator activé sur `/actuator/health` pour tous les environnements
8. **Conteneurisation** : Images officielles MySQL 8.3, MongoDB 7.0 et Eclipse Temurin 21
9. **Registry** : L'image de production est hébergée sur GitHub Container Registry

## 🔧 Variables d'environnement par profil

### Développement (`dev`)
- Aucune variable requise (configuration par défaut dans `application-dev.properties`)
- MongoDB local requis sur `localhost:27017`

### Pré-production (`preprod`)
- Variables préfixées `PREPROD_*` dans docker-compose.yml
- Configuration automatique via Docker Compose

### Production (`prod`)
- Variables définies dans `.env.prod`
- Sécurité renforcée avec conteneurs en lecture seule