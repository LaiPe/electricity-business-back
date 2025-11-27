package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.Place;
import com.laipe.electricitybusiness.repository.PlaceRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PlaceService extends GenericJPAService<Place, Long> {

    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository repository) {
        super(repository);
        this.placeRepository = repository;
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
}
