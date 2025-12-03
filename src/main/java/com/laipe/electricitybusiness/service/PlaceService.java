package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.Place;
import com.laipe.electricitybusiness.repository.ChargingStationRepository;
import com.laipe.electricitybusiness.repository.PlaceRepository;
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

    public PlaceService(
            PlaceRepository repository,
            ChargingStationRepository chargingStationRepository,
            ChargingStationService chargingStationService
    ) {
        super(repository);
        this.placeRepository = repository;
        this.chargingStationRepository = chargingStationRepository;
        this.chargingStationService = chargingStationService;
    }

    public List<Place> getAllByOwnerId(Long ownerId) {
        return placeRepository.findAllNotDeletedByOwnerId(ownerId);
    }

    @Override
    public Place create(Place entity) {
        entity.setCreatedAt(LocalDateTime.now());
        return super.create(entity);
    }

    @Override
    public List<Place> getAll() {
        return placeRepository.findAllNotDeleted();
    }

    @Override
    public Optional<Place> deleteById(Long id) {
        // First, soft delete all associated charging stations
        chargingStationRepository.findAllByPlaceIdAndDeletedAtIsNull(id)
                .forEach(station -> {
                    chargingStationService.deleteById(station.getId());
                });

        return placeRepository.findById(id)
                .map(place -> {
                    place.setDeletedAt(LocalDateTime.now());
                    return placeRepository.save(place);
                });
    }
}
