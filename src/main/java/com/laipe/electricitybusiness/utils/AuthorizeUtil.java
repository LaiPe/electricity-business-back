package com.laipe.electricitybusiness.utils;

import com.laipe.electricitybusiness.service.BookingService;
import com.laipe.electricitybusiness.service.ChargingStationService;
import com.laipe.electricitybusiness.service.PlaceService;
import com.laipe.electricitybusiness.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizeUtil {
    private final SecurityUtil securityUtil;
    private final VehicleService vehicleService;
    private final PlaceService placeService;
    private final ChargingStationService chargingStationService;
    private final BookingService bookingService;

    public boolean isOwnerOfVehicle(Long vehicleId) {
        // Récupérer l'id de l'utilisateur courant depuis le contexte de sécurité
        Long userId = securityUtil.getUserIdFromAuthentification();
        if (userId == null) {
            log.warn("AuthorizeUtil.isOwnerOfVehicle: Utilisateur non authentifié tente d'accéder au véhicule ID: {}", vehicleId);
            return false;
        }

        boolean isOwner = vehicleService.getById(vehicleId)
                .map(vehicle -> {
                    boolean owns = vehicle.getOwner().getId().equals(userId);
                    if (!owns) {
                        log.warn("AuthorizeUtil.isOwnerOfVehicle: Utilisateur ID: {} NON AUTORISÉ pour le véhicule ID: {} (propriétaire: {})",
                                 userId, vehicleId, vehicle.getOwner().getId());
                    }
                    return owns;
                })
                .orElseGet(() -> {
                    log.warn("AuthorizeUtil.isOwnerOfVehicle: Véhicule ID: {} INTROUVABLE pour l'utilisateur ID: {}", vehicleId, userId);
                    return false;
                });

        return isOwner;
    }

    public boolean isOwnerOfPlace(Long placeId) {
        // Récupérer l'id de l'utilisateur courant depuis le contexte de sécurité
        Long userId = securityUtil.getUserIdFromAuthentification();
        if (userId == null) {
            log.warn("AuthorizeUtil.isOwnerOfPlace: Utilisateur non authentifié tente d'accéder au lieu ID: {}", placeId);
            return false;
        }

        boolean isOwner = placeService.getById(placeId)
                .map(place -> {
                    boolean owns = place.getOwner().getId().equals(userId);
                    if (!owns) {
                        log.warn("AuthorizeUtil.isOwnerOfPlace: Utilisateur ID: {} NON AUTORISÉ pour le lieu ID: {} (propriétaire: {})",
                                 userId, placeId, place.getOwner().getId());
                    }
                    return owns;
                })
                .orElseGet(() -> {
                    log.warn("AuthorizeUtil.isOwnerOfPlace: Lieu ID: {} INTROUVABLE pour l'utilisateur ID: {}", placeId, userId);
                    return false;
                });

        return isOwner;
    }

    public boolean isOwnerOfChargingStation(Long stationId) {
        Long userId = securityUtil.getUserIdFromAuthentification();

        boolean isOwner = chargingStationService.getById(stationId)
                .map(station -> {
                    boolean owns = isOwnerOfPlace(station.getPlace().getId());
                    if (!owns && userId != null) {
                        log.warn("AuthorizeUtil.isOwnerOfChargingStation: Utilisateur ID: {} NON AUTORISÉ pour la station ID: {} (lieu: {})",
                                 userId, stationId, station.getPlace().getId());
                    }
                    return owns;
                })
                .orElseGet(() -> {
                    if (userId != null) {
                        log.warn("AuthorizeUtil.isOwnerOfChargingStation: Station ID: {} INTROUVABLE pour l'utilisateur ID: {}", stationId, userId);
                    }
                    return false;
                });

        return isOwner;
    }

    public boolean isStationOwnerOfBooking(Long bookingId) {
        Long userId = securityUtil.getUserIdFromAuthentification();

        boolean isOwner = bookingService.getById(bookingId)
                .map(booking -> {
                    boolean owns = isOwnerOfChargingStation(booking.getStation().getId());
                    if (!owns && userId != null) {
                        log.warn("AuthorizeUtil.isStationOwnerOfBooking: Utilisateur ID: {} NON AUTORISÉ comme propriétaire de station pour la réservation ID: {}",
                                 userId, bookingId);
                    }
                    return owns;
                })
                .orElseGet(() -> {
                    if (userId != null) {
                        log.warn("AuthorizeUtil.isStationOwnerOfBooking: Réservation ID: {} INTROUVABLE pour l'utilisateur ID: {}", bookingId, userId);
                    }
                    return false;
                });

        return isOwner;
    }

    public boolean isVehicleOwnerOfBooking(Long bookingId) {
        Long userId = securityUtil.getUserIdFromAuthentification();

        boolean isOwner = bookingService.getById(bookingId)
                .map(booking -> {
                    boolean owns = isOwnerOfVehicle(booking.getVehicle().getId());
                    if (!owns && userId != null) {
                        log.warn("AuthorizeUtil.isVehicleOwnerOfBooking: Utilisateur ID: {} NON AUTORISÉ comme propriétaire de véhicule pour la réservation ID: {}",
                                 userId, bookingId);
                    }
                    return owns;
                })
                .orElseGet(() -> {
                    if (userId != null) {
                        log.warn("AuthorizeUtil.isVehicleOwnerOfBooking: Réservation ID: {} INTROUVABLE pour l'utilisateur ID: {}", bookingId, userId);
                    }
                    return false;
                });

        return isOwner;
    }

    public boolean isPartOfBooking(Long bookingId) {
        Long userId = securityUtil.getUserIdFromAuthentification();

        boolean isPartOf = bookingService.getById(bookingId)
                .map(booking -> {
                    boolean isStationOwner = isOwnerOfChargingStation(booking.getStation().getId());
                    boolean isVehicleOwner = isOwnerOfVehicle(booking.getVehicle().getId());
                    boolean participates = isStationOwner || isVehicleOwner;

                    if (!participates && userId != null) {
                        log.warn("AuthorizeUtil.isPartOfBooking: Utilisateur ID: {} NON AUTORISÉ pour la réservation ID: {} (ni propriétaire de station ni de véhicule)",
                                 userId, bookingId);
                    }

                    return participates;
                })
                .orElseGet(() -> {
                    if (userId != null) {
                        log.warn("AuthorizeUtil.isPartOfBooking: Réservation ID: {} INTROUVABLE pour l'utilisateur ID: {}", bookingId, userId);
                    }
                    return false;
                });

        return isPartOf;
    }
}
