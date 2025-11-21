# ⚡ Electricity Business - Backend API

**Application de gestion de stations de recharge électrique**

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green?logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.3-blue?logo=mysql)
![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green?logo=mongodb)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)

## 📋 Vue d'ensemble

Electricity Business est une API REST moderne développée avec Spring Boot qui permet de gérer un écosystème complet de stations de recharge pour véhicules électriques. Le projet offre une plateforme où les propriétaires de stations peuvent mettre leurs bornes à disposition et où les propriétaires de véhicules électriques peuvent réserver des créneaux de recharge.

### 🎯 Fonctionnalités principales

- **🔐 Authentification JWT** : Système sécurisé avec cookies HTTP-only
- **👥 Gestion des utilisateurs** : Inscription, vérification, profils utilisateurs
- **🚗 Gestion des véhicules** : Catalogue de modèles et véhicules personnels
- **📍 Gestion des lieux** : Création et gestion de lieux de recharge
- **⚡ Stations de recharge** : CRUD complet avec géolocalisation
- **📅 Système de réservation** : Réservations avec états et évaluations
- **🗺️ Recherche géospatiale** : Stations proches via MongoDB
- **🔍 Recherche avancée** : Stations libres par créneaux horaires

## 🛠️ Stack technique

### Backend
- **Java 21** avec Eclipse Temurin
- **Spring Boot 3.5.3** (Web, Security, Data JPA, Data MongoDB)
- **Spring Security** avec authentification JWT
- **MapStruct** pour le mapping DTO/Entity
- **Lombok** pour la réduction du boilerplate

### Bases de données
- **MySQL 8.3** : Données relationnelles (utilisateurs, véhicules, réservations)
- **MongoDB 7.0** : Données géospatiales (stations de recharge)
- **H2** : Base en mémoire pour le développement

### DevOps & Déploiement
- **Docker & Docker Compose** : Conteneurisation multi-environnements
- **Maven** : Gestion des dépendances et build
- **GitHub Container Registry** : Images de production

## 🚀 Démarrage rapide

### Prérequis
- Java 21+
- Maven 3.9+
- Docker & Docker Compose (pour preprod/prod)
- MongoDB local (pour dev)

### Environnement de développement

```bash
# Cloner le projet
git clone https://github.com/LaiPe/electricity-business-back.git
cd electricity-business-back

# Lancer en mode développement (H2 + MongoDB local)
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

L'application sera accessible sur `http://localhost:8080`

### Autres environnements

Pour les environnements de pré-production et production avec Docker, consultez le **[Guide des environnements](ENVIRONNEMENTS.md)**.

## 📚 Documentation

| Document | Description |
|----------|-------------|
| **[🔧 ENVIRONNEMENTS.md](ENVIRONNEMENTS.md)** | Guide complet des 3 environnements d'exécution (dev, preprod, prod) |
| **[📡 ENDPOINTS.md](ENDPOINTS.md)** | Documentation détaillée de tous les endpoints de l'API |

### 🔗 Endpoints principaux

- **Authentication** : `/api/auth/*` - Login, register, logout, vérification
- **Users** : `/api/users/*` - Gestion des utilisateurs et profils
- **Vehicles** : `/api/vehicles/*` - Véhicules et modèles
- **Places** : `/api/places/*` - Lieux de recharge
- **Stations** : `/api/stations/*` - Stations de recharge avec géolocalisation
- **Bookings** : `/api/bookings/*` - Système de réservation complet

## 🏗️ Architecture

```
electricity-business-back/
├── src/main/java/com/laipe/electricitybusiness/
│   ├── config/          # Configuration Spring Security, CORS, etc.
│   ├── controller/      # Controllers REST avec validation
│   ├── dto/            # Data Transfer Objects avec MapStruct
│   ├── model/          # Entités JPA et MongoDB
│   ├── repository/     # Repositories JPA et MongoDB
│   ├── service/        # Logique métier
│   └── utils/          # Utilitaires (JWT, validation, etc.)
├── src/main/resources/
│   ├── application-{env}.properties  # Configuration par environnement
│   └── data/           # Données d'initialisation
├── docker-compose.yml           # Configuration preprod
├── docker-compose.prod.yml      # Configuration production
└── Dockerfile                   # Image Docker de l'application
```

### 🎭 Rôles et permissions

- **USER** : Peut gérer ses véhicules, lieux, stations et réservations
- **ADMIN** : Accès complet à toutes les ressources + gestion des utilisateurs

### 🔒 Sécurité

- Authentification par **JWT dans cookies HTTP-only**
- Hashage des mots de passe avec **BCrypt**
- **CORS configuré** pour les frontends autorisés
- **Validation des données** avec Spring Validation
- **Filtres de sécurité** pour les utilisateurs bannis/non vérifiés

## 🌐 Intégration Frontend

L'API est conçue pour être consommée par des applications frontend modernes (React, Vue, Angular). Elle utilise :

- **Cookies HTTP-only** pour l'authentification (pas besoin de gérer les tokens manuellement)
- **CORS** configuré pour `localhost:3000` et `localhost:5173`
- **DTOs structurés** avec toutes les données nécessaires
- **Codes de statut HTTP appropriés**
- **Gestion d'erreurs standardisée**

## 🧪 Tests et développement

```bash
# Tests unitaires
mvn test

# Build de production
mvn clean package

# Lancement avec profil spécifique
mvn spring-boot:run "-Dspring-boot.run.profiles=preprod"
```

### 🔍 Outils de debug

- **H2 Console** (dev) : `http://localhost:8080/h2-console`
- **Actuator Health** : `http://localhost:8080/actuator/health`
- **Logs détaillés** en développement

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/amazing-feature`)
3. Commit les changements (`git commit -m 'Add amazing feature'`)
4. Push sur la branche (`git push origin feature/amazing-feature`)
5. Ouvrir une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

---

**Développé avec ❤️ par [LaiPe](https://github.com/LaiPe)**