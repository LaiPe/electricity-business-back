package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.Place;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.repository.ChargingStationRepository;
import com.laipe.electricitybusiness.repository.PlaceRepository;
import com.laipe.electricitybusiness.repository.UserRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlaceService extends GenericJPAService<Place, Long> {

    private final ChargingStationService chargingStationService;

    private final PlaceRepository placeRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final UserRepository userRepository;

    public PlaceService(
            PlaceRepository repository,
            ChargingStationRepository chargingStationRepository,
            ChargingStationService chargingStationService,
            UserRepository userRepository) {
        super(repository);
        this.placeRepository = repository;
        this.chargingStationRepository = chargingStationRepository;
        this.chargingStationService = chargingStationService;
        this.userRepository = userRepository;
    }

    public List<Place> getAllByOwnerId(Long ownerId) {
        return placeRepository.findAllNotDeletedByOwnerId(ownerId);
    }

    @Override
    public Place create(Place entity) {
        userRepository.findById(entity.getOwner().getId())
                .filter(e -> e.getDeletedAt() != null) // Filter undeleted user
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Cannot create a place for a deleted user.");
                });

        entity.setCreatedAt(LocalDateTime.now());
        return super.create(entity);
    }

    @Override
    public List<Place> getAll() {
        return placeRepository.findAllNotDeleted();
    }

    @Override
    public Optional<Place> update(Place entity, Long id) {
        // Verify that the place is not soft-deleted, and if it's not, get user id from existing place
        Long userId = placeRepository.findById(id)
                .filter(place -> place.getDeletedAt() == null) // Filter deleted place
                .map(Place::getOwner)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot update a deleted place."));

        // Verify that the user is not deleted
        userRepository.findById(userId)
                .filter(e -> e.getDeletedAt() != null) // Filter undeleted user
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Cannot update a place for a deleted user.");
                });

        // Proceed with the update
        super.update(entity, id);

        // As a place is supposed to be fetched with its charging stations (eager),
        // by using the generic update method return value, we will be using findById repository method,
        // that would include deleted charging stations as well,
        // so we need to filter them out here
        return placeRepository.findById(id)
                .map(this::filterDeletedStations);
    }

    @Override
    public Optional<Place> deleteById(Long id) {
        // Verify that the place isn't already soft-deleted
        placeRepository.findById(id)
                .filter(e -> e.getDeletedAt() != null) // Filter undeleted place
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Place is already deleted.");
                });

        // First, soft delete all associated charging stations
        chargingStationRepository.findAllByPlaceIdAndDeletedAtIsNull(id)
                .forEach(station -> {
                    chargingStationService.deleteById(station.getId());
                });

        // Then, soft delete the place itself
        return placeRepository.findById(id)
                .map(place -> {
                    place.setDeletedAt(LocalDateTime.now());
                    return placeRepository.save(place);
                });
    }

    /**
     * Filters out soft-deleted charging stations from a Place.
     * Uses removeIf to modify the existing collection instead of replacing it,
     * which would cause issues with Hibernate's orphanRemoval.
     * @param place the place to filter
     * @return the place with only non-deleted charging stations
     */
    private Place filterDeletedStations(Place place) {
        if (place.getChargingStations() != null) {
            place.getChargingStations().removeIf(cs -> cs.getDeletedAt() != null);
        }
        return place;
    }
}
