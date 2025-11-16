# API - Liste des endpoints

## 1. Authentification & utilisateur

- POST /auth/register
  - Description : Inscription ; création d'un utilisateur en statut PENDING, envoi d'un code de validation par email
  - Auth : non
  - Body : { "email", "password", "fullName"?, "phone"? }
  - Response : 201 Created

- POST /auth/verify
  - Description : Validation de l'inscription via code reçu par mail
  - Auth : non
  - Body : { "email", "code" }
  - Response : 200 OK

- POST /auth/login
  - Description : Authentification, renvoie token JWT et informations utilisateur
  - Auth : non
  - Body : { "email", "password" }
  - Response : 200 { "token", "user" }

- POST /auth/logout
  - Description : Révocation / blacklist du token (côté serveur) ou suppression côté client
  - Auth : oui
  - Response : 204 No Content

- GET /users/me
  - Description : Récupère le profil courant
  - Auth : oui
  - Response : 200 { user }

- PUT /users/me
  - Description : Met à jour des champs non sensibles du profil
  - Auth : oui
  - Body : { "fullName"?, "phone"?, ... }
  - Response : 200 { user }

---

## 2. Modèles et véhicules

- GET /vehicle-models
  - Description : Liste / recherche des modèles (source `vehicle-models.json` possible)
  - Auth : optionnel
  - Query : ?make=&model=&year=&page=&size=

- POST /vehicles
  - Description : Ajouter un véhicule pour l'utilisateur courant
  - Auth : oui
  - Body : { "modelId", "plate"?, "nickname"? }
  - Response : 201

- GET /vehicles
  - Description : Lister véhicules de l'utilisateur courant
  - Auth : oui

- PUT /vehicles/{vehicleId}
  - Description : Modifier véhicule
  - Auth : oui (propriétaire du véhicule)

- DELETE /vehicles/{vehicleId}
  - Description : Supprimer véhicule
  - Auth : oui (propriétaire)

---

## 3. Lieux (Place)

- POST /places
  - Description : Créer un lieu de recharge (par ex. parking, station)
  - Auth : oui (ROLE_OWNER / ROLE_ADMIN)
  - Body : { "name", "address", "latitude", "longitude", "description"? }
  - Response : 201

- PUT /places/{placeId}
  - Description : Modifier un lieu
  - Auth : oui (owner/admin)

- GET /places/{placeId}
  - Description : Détails d'un lieu, avec bornes associées
  - Auth : optionnel

- GET /places
  - Description : Rechercher / filtrer lieux
  - Query : ?q=&lat=&lon=&radius=&page=&size=

- DELETE /places/{placeId}
  - Description : Supprimer un lieu (contrainte : pas d'entité critique liée)
  - Auth : oui (owner/admin)

---

## 4. Bornes de recharge (ChargingStation)

- POST /stations
  - Description : Ajouter une borne attachée à un lieu
  - Auth : oui (ROLE_OWNER / ROLE_ADMIN)
  - Body : { "placeId", "name", "connectorType", "maxPowerKw", "enabled" }
  - Response : 201

- PUT /stations/{stationId}
  - Description : Modifier les caractéristiques d'une borne
  - Auth : oui (owner/admin)

- GET /stations/{stationId}
  - Description : Détails d'une borne (tarifs, disponibilité, statut)
  - Auth : optionnel

- DELETE /stations/{stationId}
  - Description : Supprimer une borne uniquement si elle n'a aucune réservation passée ou future
  - Auth : oui (owner/admin)
  - Responses : 204 ou 409 Conflict (si bookings existantes)

---

## 5. Tarifs horaires (Rates / Pricing)

- POST /stations/{stationId}/rates
  - Description : Définir ou remplacer la grille tarifaire (liste d'intervalles horaires / prix)
  - Auth : oui (owner/admin)
  - Body : [ { "fromHour" (HH:mm), "toHour" (HH:mm), "pricePerKwh" (decimal) } ]

- GET /stations/{stationId}/rates
  - Description : Récupérer tarifs actuels
  - Auth : optionnel

- PATCH /stations/{stationId}/rates/{rateId}
  - Description : Modifier un tarif spécifique
  - Auth : oui (owner/admin)

---

## 6. Recherche de bornes autour de soi (pour affichage carte)

- GET /stations/nearby
  - Description : Rechercher bornes disponibles autour d'un point géographique
  - Auth : optionnel
  - Query : ?lat={lat}&lon={lon}&radius={meters}&connectorType=&minPower=&availableOnly=true&page=&size=
  - Response : 200 [ { stationId, placeId, lat, lon, available (bool), connectors[], currentTariff } ]

---

## 7. Réservations (Booking)

- POST /bookings
  - Description : Effectuer une réservation (demande)
  - Auth : oui
  - Body : { "stationId", "vehicleId", "startAt", "endAt", "estimatedKwh"? }
  - Response : 201 { "bookingId", "state" }

- GET /bookings
  - Description : Lister réservations de l'utilisateur (avec filtres)
  - Auth : oui
  - Query : ?state=ACTIVE,PAST,ALL&from=&to=&stationId=&page=&size=&fields=

- GET /bookings/{bookingId}
  - Description : Détails d'une réservation (autorisé au propriétaire de la réservation, propriétaire de la borne ou admin)
  - Auth : oui

- PATCH /bookings/{bookingId}/accept
  - Description : Accepter une réservation (opérationnel/manager de la borne)
  - Auth : oui (station owner)
  - Response : 200

- PATCH /bookings/{bookingId}/reject
  - Description : Refuser une réservation (avec motif optionnel)
  - Auth : oui (station owner)
  - Body : { "reason"? }

- PATCH /bookings/{bookingId}/cancel
  - Description : Annuler une réservation (par le client avant le début)
  - Auth : oui (booking owner)

- GET /bookings/current
  - Description : Réservations en cours pour l'utilisateur
  - Auth : oui

- GET /bookings/past
  - Description : Réservations passées; possibilité de filtrer colonnes/infos
  - Auth : oui
  - Query : ?from=&to=&stationId=&page=&size=&fields=

---

## 8. Reçus PDF & export Excel

- GET /bookings/{bookingId}/receipt.pdf
  - Description : Générer / télécharger le reçu PDF pour une réservation acceptée/terminée
  - Auth : oui (booking owner / station owner / admin)
  - Response : application/pdf

- GET /bookings/export.xlsx
  - Description : Exporter réservations (passées ou filtrées) au format Excel
  - Auth : oui (owner/admin)
  - Query : ?from=&to=&stationId=&userId=&fields=
  - Response : application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

---

## 9. Webhooks / Notifications (optionnel)

- POST /webhooks/booking-updates
  - Description : Endpoint pour notifications externes (ex : système de paiement, opérateur de borne)
  - Auth : signature/secret (pas JWT)

---

## Contrat minimal (par ressource)
- Inputs : JSON pour POST/PUT/PATCH, paramètres query pour filtres
- Outputs : JSON standard { "data": ..., "meta"?: { pagination } } ou types médias (PDF/XLSX)
- Codes d'erreur usuels : 400 Validation, 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict, 500 Server Error

## Cas limites & règles métier importantes
- Suppression de borne (`DELETE /stations/{id}`) : refusée (409) si réservation passée ou future liée.
- Création de réservation : vérifier la disponibilité atomiquement pour éviter double-booking.
- Validation inscription : code unique temporaire, TTL court.
- Reçus PDF : seulement pour réservations acceptées/terminées.
- Export Excel : accès restreint (ROLE_OWNER pour ses stations, ROLE_ADMIN global)
- Les modifications sensibles (tarifs, suppression) nécessitent rôle owner/admin et ownership vérifié.

## Mapping besoins fonctionnels -> endpoints (couverture)
- S'inscrire : POST /auth/register ✅
- Se connecter : POST /auth/login ✅
- Se déconnecter : POST /auth/logout ✅
- Valider inscription : POST /auth/verify ✅
- Accepter/Refuser réservation : PATCH /bookings/{id}/accept, /reject ✅
- Effectuer réservation : POST /bookings ✅
- Trouver une borne autour de soi (carte) : GET /stations/nearby ✅
- Ajouter/modifier lieu : POST /places, PUT /places/{id} ✅
- Ajouter/modifier borne : POST /stations, PUT /stations/{id} ✅
- Définir tarifs horaires : POST /stations/{id}/rates ✅
- Obtenir reçu PDF : GET /bookings/{id}/receipt.pdf ✅
- Voir réservations en cours : GET /bookings/current ✅
- Voir réservations passées (filtrage) : GET /bookings/past ✅
- Exporter réservations au format Excel : GET /bookings/export.xlsx ✅
- Supprimer borne si non réservée : DELETE /stations/{id} (logique 409 si réservée) ✅

---

Notes / étapes suivantes conseillées
- Implémenter des tests d'intégration pour : création de réservation (concurrence), suppression de borne, génération PDF et export Excel.
- Documenter les schémas JSON précis (DTO) et ajouter OpenAPI/Swagger.


