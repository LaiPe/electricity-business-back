package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.controller.handler.InvalidBookingState;
import com.laipe.electricitybusiness.model.Booking;
import com.laipe.electricitybusiness.model.ChargingStation;
import com.laipe.electricitybusiness.model.Place;
import com.laipe.electricitybusiness.repository.BookingRepository;
import com.laipe.electricitybusiness.repository.ChargingStationRepository;
import com.laipe.electricitybusiness.repository.PlaceRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import com.laipe.electricitybusiness.utils.DateUtil;
import com.laipe.electricitybusiness.utils.GeolocatorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChargingStationService extends GenericJPAService<ChargingStation, Long> {

    private final ChargingStationRepository stationRepository;
    private final BookingRepository bookingRepository;

    private final GeolocatorUtil geolocatorUtil;
    private final DateUtil dateUtil;

    private final PlaceRepository placeRepository;

    public ChargingStationService(
            ChargingStationRepository stationRepository,
            BookingRepository bookingRepository,
            PlaceRepository placeRepository,
            GeolocatorUtil geolocatorUtil,
            DateUtil dateUtil
            ) {
        super(stationRepository);
        this.stationRepository = stationRepository;
        this.bookingRepository = bookingRepository;
        this.geolocatorUtil = geolocatorUtil;
        this.dateUtil = dateUtil;
        this.placeRepository = placeRepository;
    }

    /**
     * Trouve toutes les bornes dans un rayon donné autour d'un point géographique
     * @param longitude Longitude du point central
     * @param latitude Latitude du point central
     * @param radius Rayon de recherche en kilomètres
     * @return Liste des bornes trouvées dans le rayon spécifié
     */
    public List<ChargingStation> getNearbyStations(BigDecimal longitude, BigDecimal latitude, Integer radius) {
        if (longitude == null || latitude == null) {
            throw new IllegalArgumentException("Les coordonnées ne peuvent pas être null");
        }
        if (longitude.doubleValue() < -180 || longitude.doubleValue() > 180) {
            throw new IllegalArgumentException("La longitude doit être comprise entre -180 et 180");
        }
        if (latitude.doubleValue() < -90 || latitude.doubleValue() > 90) {
            throw new IllegalArgumentException("La latitude doit être comprise entre -90 et 90");
        }

        return stationRepository.findAllNotDeletedWithBookings().stream()
                .filter(borne -> geolocatorUtil.calculateDistance(
                        latitude,
                        longitude,
                        borne.getLatitude(),
                        borne.getLongitude())
                        <= radius.doubleValue()
                )
                .toList();
    }



    /**
     * Trouve toutes les bornes libres à un moment donné
     * @param searchStart Moment de début de la recherche
     * @param searchEnd Moment de fin de la recherche
     * @return Liste des bornes libres au moment spécifié
     */
    public List<ChargingStation> getFreeStations(LocalDateTime searchStart, LocalDateTime searchEnd) {
        if (searchStart == null || searchEnd == null) {
            throw new IllegalArgumentException("Le temps ne peut pas être null");
        }

        List<ChargingStation> allStations = stationRepository.findAllNotDeletedWithBookings();
        List<Booking> activeBookings = bookingRepository.findAll().stream()
                .filter(Booking::isActive)
                .filter(r -> dateUtil.doOverlap(r.getStartDate(), r.getExpectedEndDate(), searchStart, searchEnd))
                .toList();

        return allStations.stream()
                .filter(borne -> activeBookings.stream()
                        .noneMatch(reservation -> reservation.getStation().getId().equals(borne.getId())))
                .toList();
    }

    /**
     * Trouve toutes les bornes libres dans un rayon donné autour d'un point géographique à un moment donné.
     * Cette méthode combine les résultats de get_free_borne et get_nearby_borne pour obtenir les bornes
     * qui sont à la fois libres et dans le rayon spécifié.
     *
     * @param longitude Longitude du point central en degrés décimaux (-180 à 180)
     * @param latitude Latitude du point central en degrés décimaux (-90 à 90)
     * @param rayon Rayon de recherche en kilomètres
     * @param searchStart Moment de début de la recherche
     * @param searchEnd Moment de fin de la recherche
     * @return Liste des bornes libres trouvées dans le rayon spécifié au moment donné
     * @throws IllegalArgumentException si le temps est null ou si les coordonnées sont invalides
     */
    public List<ChargingStation> getFreeNearbyStations(
            BigDecimal longitude,
            BigDecimal latitude,
            Integer rayon,
            LocalDateTime searchStart,
            LocalDateTime searchEnd
    ) {
        if (searchStart == null || searchEnd == null) {
            throw new IllegalArgumentException("Le temps ne peut pas être null");
        }
        if (longitude == null || latitude == null) {
            throw new IllegalArgumentException("Les coordonnées ne peuvent pas être null");
        }
        if (longitude.doubleValue() < -180 || longitude.doubleValue() > 180) {
            throw new IllegalArgumentException("La longitude doit être comprise entre -180 et 180");
        }
        if (latitude.doubleValue() < -90 || latitude.doubleValue() > 90) {
            throw new IllegalArgumentException("La latitude doit être comprise entre -90 et 90");
        }

        // Obtenir les bornes libres au moment donné
        List<ChargingStation> freeBornes = getFreeStations(searchStart, searchEnd);

        // Obtenir les bornes dans le rayon spécifié
        List<ChargingStation> nearbyBornes = getNearbyStations(longitude, latitude, rayon);

        // Retourner l'intersection des deux listes
        return freeBornes.stream()
                .filter(freeBorne -> nearbyBornes.stream()
                        .anyMatch(nearbyBorne -> nearbyBorne.getId().equals(freeBorne.getId())))
                .toList();
    }

    @Override
    public ChargingStation create(ChargingStation entity) {
        // Verify that the place is not deleted
        placeRepository.findById(entity.getPlace().getId())
                .filter(place -> place.getDeletedAt() != null) // Filter undeleted place
                .ifPresent(place -> {
                    throw new IllegalArgumentException("Cannot move station to a deleted place.");
                });

        entity.setCreatedAt(LocalDateTime.now());
        return super.create(entity);
    }

    @Override
    public Optional<ChargingStation> update(ChargingStation entity, Long id) {
        // Verify that the station is not soft-deleted, and if it's not, get place id from existing station
        Long placeId = stationRepository.findById(id)
                .filter(station -> station.getDeletedAt() == null) // Filter deleted station
                .map(ChargingStation::getPlace)
                .map(Place::getId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot update a deleted station."));


        // Verify that the place is not deleted
        placeRepository.findById(placeId)
                .filter(place -> place.getDeletedAt() != null) // Filter undeleted place
                .ifPresent(place -> {
                    throw new IllegalArgumentException("Cannot move station to a deleted place.");
                });

        return super.update(entity, id);
    }

    @Override
    public List<ChargingStation> getAll() {
        return stationRepository.findAllNotDeleted();
    }

    @Override
    public Optional<ChargingStation> deleteById(Long id) {
        // Verify that the station isn't already soft-deleted
        stationRepository.findById(id)
                .filter(e -> e.getDeletedAt() != null) // Filter undeleted station
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Station is already deleted.");
                });

        // Verify that there is no active or future booking for this station
        bookingRepository.findAllByStationId(id)
                .forEach(booking -> {
                    // If there is an active or future booking, throw an exception
                    if (booking.isActive() || booking.getStartDate().isAfter(LocalDateTime.now())) {
                        throw new InvalidBookingState("Cannot delete station with active or future bookings.");
                    }
                });

        // Then, soft delete the station
        return stationRepository.findById(id)
                .map(station -> {
                    station.setDeletedAt(LocalDateTime.now());
                    return stationRepository.save(station);
                });
    }
}
