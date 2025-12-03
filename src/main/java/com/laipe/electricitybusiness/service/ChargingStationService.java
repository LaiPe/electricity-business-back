package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.controller.handler.InvalidBookingState;
import com.laipe.electricitybusiness.dto.chargingstations.GetChargingStationDTO;
import com.laipe.electricitybusiness.dto.chargingstations.GetChargingStationMapper;
import com.laipe.electricitybusiness.model.Booking;
import com.laipe.electricitybusiness.model.ChargingStation;
import com.laipe.electricitybusiness.repository.BookingRepository;
import com.laipe.electricitybusiness.repository.ChargingStationRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import com.laipe.electricitybusiness.utils.DateUtil;
import com.laipe.electricitybusiness.utils.GeolocatorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChargingStationService extends GenericJPAService<ChargingStation, Long> {

    private final ChargingStationRepository stationRepository;
    private final BookingRepository bookingRepository;

    private final GeolocatorUtil geolocatorUtil;
    private final DateUtil dateUtil;

    private final GetChargingStationMapper getChargingStationMapper;

    public ChargingStationService(
            ChargingStationRepository stationRepository,
            BookingRepository bookingRepository,
            GeolocatorUtil geolocatorUtil,
            DateUtil dateUtil,
            GetChargingStationMapper getChargingStationMapper
    ) {
        super(stationRepository);
        this.stationRepository = stationRepository;
        this.bookingRepository = bookingRepository;
        this.geolocatorUtil = geolocatorUtil;
        this.dateUtil = dateUtil;
        this.getChargingStationMapper = getChargingStationMapper;
    }

    /**
     * Trouve toutes les bornes dans un rayon donné autour d'un point géographique
     * @param longitude Longitude du point central
     * @param latitude Latitude du point central
     * @param radius Rayon de recherche en kilomètres
     * @return Liste des bornes trouvées dans le rayon spécifié
     */
    public List<GetChargingStationDTO> getNearbyStations(BigDecimal longitude, BigDecimal latitude, Integer radius) {
        if (longitude == null || latitude == null) {
            throw new IllegalArgumentException("Les coordonnées ne peuvent pas être null");
        }
        if (longitude.doubleValue() < -180 || longitude.doubleValue() > 180) {
            throw new IllegalArgumentException("La longitude doit être comprise entre -180 et 180");
        }
        if (latitude.doubleValue() < -90 || latitude.doubleValue() > 90) {
            throw new IllegalArgumentException("La latitude doit être comprise entre -90 et 90");
        }

        return stationRepository.findAllNotDeleted().stream()
                .filter(borne -> geolocatorUtil.calculateDistance(latitude, longitude, borne.getLatitude(), borne.getLongitude()) <= radius.doubleValue())
                .map(getChargingStationMapper::toDto)
                .toList();
    }



    /**
     * Trouve toutes les bornes libres à un moment donné
     * @param searchStart Moment de début de la recherche
     * @param searchEnd Moment de fin de la recherche
     * @return Liste des bornes libres au moment spécifié
     */
    public List<GetChargingStationDTO> getFreeStations(LocalDateTime searchStart, LocalDateTime searchEnd) {
        if (searchStart == null || searchEnd == null) {
            throw new IllegalArgumentException("Le temps ne peut pas être null");
        }

        List<ChargingStation> allStations = stationRepository.findAllNotDeleted();
        List<Booking> activeBookings = bookingRepository.findAll().stream()
                .filter(Booking::isActive)
                .filter(r -> dateUtil.doOverlap(r.getStartDate(), r.getExpectedEndDate(), searchStart, searchEnd))
                .toList();

        return allStations.stream()
                .filter(borne -> activeBookings.stream()
                        .noneMatch(reservation -> reservation.getStation().getId().equals(borne.getId())))
                .map(getChargingStationMapper::toDto)
                .collect(Collectors.toList());
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
    public List<GetChargingStationDTO> getFreeNearbyStations(
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
        List<GetChargingStationDTO> freeBornes = getFreeStations(searchStart, searchEnd);

        // Obtenir les bornes dans le rayon spécifié
        List<GetChargingStationDTO> nearbyBornes = getNearbyStations(longitude, latitude, rayon);

        // Retourner l'intersection des deux listes
        return freeBornes.stream()
                .filter(freeBorne -> nearbyBornes.stream()
                        .anyMatch(nearbyBorne -> nearbyBorne.getId().equals(freeBorne.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ChargingStation create(ChargingStation entity) {
        entity.setCreatedAt(LocalDateTime.now());
        return super.create(entity);
    }

    @Override
    public List<ChargingStation> getAll() {
        return stationRepository.findAllNotDeleted();
    }

    @Override
    public Optional<ChargingStation> deleteById(Long id) {
        bookingRepository.findAllByStationId(id)
                .forEach(booking -> {
                    // Si une réservation est présente ou future, on ne peut pas supprimer la borne
                    if (booking.isActive() || booking.getStartDate().isAfter(LocalDateTime.now())) {
                        throw new InvalidBookingState("Cannot delete station with active or future bookings.");
                    }
                });

        return stationRepository.findById(id)
                .map(station -> {
                    station.setDeletedAt(LocalDateTime.now());
                    return stationRepository.save(station);
                });
    }
}
