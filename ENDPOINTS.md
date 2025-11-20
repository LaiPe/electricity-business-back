# Documentation des Endpoints de l'API

Cette documentation liste tous les endpoints exposés par l'API Electricity Business.

---

## 1. Authentification (`/api/auth`)

### `POST /api/auth/login`
- **Accès** : Public
- **Corps** : `LoginDTO` (username, password)
- **Réponse** : `AuthResponse` (message + StatusUserDTO)
- **Description** : Authentifie l'utilisateur, génère un JWT et le place dans un cookie HTTP-only nommé `access_token`.

### `POST /api/auth/register`
- **Accès** : Public
- **Corps** : `RegisterDTO`
- **Réponse** : `AuthResponse` (message + StatusUserDTO)
- **Description** : Crée un nouvel utilisateur avec le rôle USER, génère un code de vérification, crée un JWT et le place dans un cookie HTTP-only.

### `POST /api/auth/verify`
- **Accès** : Authentifié (non vérifié)
- **Corps** : `String` (code de vérification)
- **Réponse** : `MessageResponse`
- **Description** : Vérifie le code de vérification de l'utilisateur courant.

### `POST /api/auth/refresh-verification-code`
- **Accès** : Authentifié (non vérifié)
- **Corps** : Aucun
- **Réponse** : `MessageResponse`
- **Description** : Génère un nouveau code de vérification pour l'utilisateur courant.

### `GET /api/auth/status`
- **Accès** : Authentifié (non vérifié ou banni)
- **Corps** : Aucun
- **Réponse** : `AuthResponse`
- **Description** : Retourne le statut d'authentification et les informations de l'utilisateur courant.

### `POST /api/auth/logout`
- **Accès** : Authentifié (non vérifié ou banni)
- **Corps** : Aucun
- **Réponse** : `204 No Content`
- **Description** : Efface le cookie `access_token` côté client.

---

## 2. Utilisateurs (`/api/users`)

### `POST /api/users`
- **Accès** : ADMIN uniquement
- **Corps** : `PostUserDTO`
- **Réponse** : `GetUserDTO`
- **Description** : Crée un nouvel utilisateur (usage administratif).

### `GET /api/users`
- **Accès** : ADMIN uniquement
- **Réponse** : `List<GetUserDTO>`
- **Description** : Liste tous les utilisateurs.

### `GET /api/users/{id}`
- **Accès** : ADMIN uniquement
- **Réponse** : `GetUserDTO`
- **Description** : Récupère un utilisateur par son ID.

### `PUT /api/users/{id}`
- **Accès** : ADMIN uniquement
- **Corps** : `PostUserDTO`
- **Réponse** : `GetUserDTO`
- **Description** : Met à jour un utilisateur par son ID.

### `DELETE /api/users/{id}`
- **Accès** : ADMIN uniquement
- **Réponse** : `204 No Content`
- **Description** : Supprime un utilisateur par son ID.

### `PATCH /api/users/{id}/ban`
- **Accès** : ADMIN uniquement
- **Réponse** : `204 No Content`
- **Description** : Banni un utilisateur par son ID.

### `PATCH /api/users/{id}/unban`
- **Accès** : ADMIN uniquement
- **Réponse** : `204 No Content`
- **Description** : Débanni un utilisateur par son ID.

### `GET /api/users/me`
- **Accès** : Authentifié
- **Réponse** : `GetUserDTO`
- **Description** : Récupère les informations de l'utilisateur courant.

### `PUT /api/users/me`
- **Accès** : Authentifié
- **Corps** : `UpdateUserDTO`
- **Réponse** : `GetUserDTO`
- **Description** : Met à jour les informations du profil de l'utilisateur courant.

### `PUT /api/users/me/username`
- **Accès** : Authentifié
- **Corps** : `UpdateUsernameDTO`
- **Réponse** : `GetUserDTO`
- **Description** : Met à jour le nom d'utilisateur.

### `PUT /api/users/me/password`
- **Accès** : Authentifié
- **Corps** : `UpdatePasswordDTO`
- **Réponse** : `GetUserDTO`
- **Description** : Met à jour le mot de passe (hashé côté service).

### `PUT /api/users/me/email`
- **Accès** : Authentifié
- **Corps** : `UpdateEmailDTO`
- **Réponse** : `GetUserDTO`
- **Description** : Met à jour l'adresse email.

### `DELETE /api/users/me/delete`
- **Accès** : Authentifié
- **Réponse** : `204 No Content`
- **Description** : Supprime le compte de l'utilisateur courant et efface le cookie d'accès.

---

## 3. Modèles de véhicule (`/api/vehicles/models`)

### `GET /api/vehicles/models`
- **Accès** : Authentifié
- **Réponse** : `List<VehicleModel>`
- **Description** : Liste tous les modèles de véhicules disponibles.

### `GET /api/vehicles/models/{id}`
- **Accès** : Authentifié
- **Réponse** : `VehicleModel`
- **Description** : Récupère un modèle de véhicule par son ID.

### `GET /api/vehicles/models/search?q={query}`
- **Accès** : Authentifié
- **Paramètres** : `q` (query string de recherche)
- **Réponse** : `List<VehicleModel>`
- **Description** : Recherche des modèles de véhicules par texte.

---

## 4. Véhicules (`/api/vehicles`)

### `POST /api/vehicles`
- **Accès** : Authentifié
- **Corps** : `PostVehicleDTO`
- **Réponse** : `GetVehicleDTO`
- **Description** : Crée un véhicule et assigne automatiquement l'utilisateur courant comme propriétaire.

### `GET /api/vehicles`
- **Accès** : Authentifié
- **Réponse** : `List<GetVehicleDTO>`
- **Description** : Liste les véhicules appartenant à l'utilisateur courant.

### `GET /api/vehicles/all`
- **Accès** : ADMIN uniquement
- **Réponse** : `List<GetVehicleDTO>`
- **Description** : Liste tous les véhicules (vue administrateur).

### `GET /api/vehicles/{id}`
- **Accès** : ADMIN ou propriétaire du véhicule 
- **Réponse** : `GetVehicleDTO`
- **Description** : Récupère un véhicule par son ID.

### `PUT /api/vehicles/{id}`
- **Accès** : ADMIN ou propriétaire du véhicule
- **Corps** : `PostVehicleDTO`
- **Réponse** : `GetVehicleDTO`
- **Description** : Met à jour un véhicule.

### `DELETE /api/vehicles/{id}`
- **Accès** : ADMIN ou propriétaire du véhicule
- **Réponse** : `204 No Content`
- **Description** : Supprime un véhicule.

---

## 5. Lieux (`/api/places`)

### `POST /api/places`
- **Accès** : Authentifié
- **Corps** : `PostPlaceDTO`
- **Réponse** : `GetPlaceDTO`
- **Description** : Crée un lieu et assigne l'utilisateur courant comme propriétaire.

### `GET /api/places`
- **Accès** : Authentifié
- **Réponse** : `List<GetPlaceDTO>`
- **Description** : Liste les lieux appartenant à l'utilisateur courant.

### `GET /api/places/all`
- **Accès** : ADMIN uniquement
- **Réponse** : `List<GetPlaceDTO>`
- **Description** : Liste tous les lieux (vue administrateur).

### `GET /api/places/{id}`
- **Accès** : ADMIN ou propriétaire du lieu 
- **Réponse** : `GetPlaceDTO`
- **Description** : Récupère un lieu par son ID.

### `PUT /api/places/{id}`
- **Accès** : ADMIN ou propriétaire du lieu
- **Corps** : `PostPlaceDTO`
- **Réponse** : `GetPlaceDTO`
- **Description** : Met à jour un lieu.

### `DELETE /api/places/{id}`
- **Accès** : ADMIN ou propriétaire du lieu
- **Réponse** : `204 No Content`
- **Description** : Supprime un lieu.

---

## 6. Stations de recharge (`/api/stations`)

### `POST /api/stations`
- **Accès** : ADMIN ou propriétaire du lieu associé
- **Corps** : `PostChargingStationDTO`
- **Réponse** : `GetChargingStationDTO`
- **Description** : Crée une station de recharge sur un lieu existant.

### `GET /api/stations/{id}`
- **Accès** : Public
- **Réponse** : `GetChargingStationDTO`
- **Description** : Récupère une station de recharge par son ID.

### `PUT /api/stations/{id}`
- **Accès** : ADMIN ou propriétaire de la station
- **Corps** : `UpdateChargingStationDTO`
- **Réponse** : `GetChargingStationDTO`
- **Description** : Met à jour une station de recharge.

### `DELETE /api/stations/{id}`
- **Accès** : ADMIN ou propriétaire de la station
- **Réponse** : `204 No Content`
- **Description** : Supprime une station de recharge.

### `GET /api/stations/nearby`
- **Accès** : Public
- **Corps** : `QueryNearbyChargingStationDTO` (longitude, latitude, radiusInKm)
- **Réponse** : `List<GetChargingStationDTO>`
- **Description** : Recherche les stations de recharge proches d'une position géographique donnée.

### `GET /api/stations/free?datetime={datetime}`
- **Accès** : Public
- **Paramètres** : `datetime` (LocalDateTime au format ISO)
- **Réponse** : `List<GetChargingStationDTO>`
- **Description** : Liste les stations de recharge libres à une date/heure donnée.

### `GET /api/stations/nearby-and-free`
- **Accès** : Public
- **Corps** : `QueryNearbyFreeChargingStationDTO` (longitude, latitude, radiusInKm, datetime)
- **Réponse** : `List<GetChargingStationDTO>`
- **Description** : Recherche les stations proches ET libres à une date/heure donnée.

---

## 7. Réservations (`/api/bookings`)

### `POST /api/bookings`
- **Accès** : ADMIN ou propriétaire du véhicule associé
- **Corps** : `PostBookingDTO`
- **Réponse** : `GetBookingDTO`
- **Description** : Crée une réservation de station pour un véhicule donné.

### `GET /api/bookings`
- **Accès** : ADMIN uniquement
- **Réponse** : `List<GetBookingDTO>`
- **Description** : Liste toutes les réservations.

### `GET /api/bookings/as-vehicle-owner`
- **Accès** : Authentifié
- **Réponse** : `List<GetBookingDTO>`
- **Description** : Liste les réservations où l'utilisateur courant est propriétaire du véhicule.

### `GET /api/bookings/as-station-owner`
- **Accès** : Authentifié
- **Réponse** : `List<GetBookingDTO>`
- **Description** : Liste les réservations où l'utilisateur courant est propriétaire de la station.

### `GET /api/bookings/{id}`
- **Accès** : ADMIN ou partie prenante de la réservation (propriétaire du véhicule ou de la station)
- **Réponse** : `GetBookingDTO`
- **Description** : Récupère une réservation par son ID (si l'utilisateur est impliqué).

### `PATCH /api/bookings/{id}/accept`
- **Accès** : ADMIN ou propriétaire de la station associée
- **Réponse** : `GetBookingDTO`
- **Description** : Accepte une réservation.

### `PATCH /api/bookings/{id}/reject`
- **Accès** : ADMIN ou propriétaire de la station associée
- **Réponse** : `GetBookingDTO`
- **Description** : Rejette une réservation.

### `PATCH /api/bookings/{id}/cancel`
- **Accès** : ADMIN ou propriétaire du véhicule associé
- **Réponse** : `GetBookingDTO`
- **Description** : Annule une réservation.

### `PATCH /api/bookings/{id}/start`
- **Accès** : ADMIN ou propriétaire du véhicule associé
- **Réponse** : `GetBookingDTO`
- **Description** : Démarre une session de recharge.

### `PATCH /api/bookings/{id}/end`
- **Accès** : ADMIN ou propriétaire du véhicule associé
- **Réponse** : `GetBookingDTO`
- **Description** : Termine une session de recharge.

### `PATCH /api/bookings/{id}/review`
- **Accès** : ADMIN ou propriétaire du véhicule associé
- **Corps** : `PostReviewBookingDTO` (reviewGrade, reviewComment)
- **Réponse** : `GetBookingDTO`
- **Description** : Ajoute une évaluation (note et commentaire) à une réservation terminée.

---

## Notes importantes

1. **Authentification** : L'API utilise JWT stocké dans un cookie HTTP-only nommé `access_token`. Ce cookie est automatiquement envoyé par le navigateur avec chaque requête.

2. **Rôles** : Deux rôles existent : `USER` (par défaut) et `ADMIN` (pour les opérations d'administration).

4. **CORS** : Configuré pour accepter les origines définies dans `application.properties` avec credentials (cookies).

5. **Endpoints publics** : Seuls les endpoints de login/register et de recherche de stations sont accessibles sans authentification.

