package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.Place;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlaceService extends GenericJPAService<Place, Long> {
    public PlaceService(JpaRepository<Place, Long> repository) {
        super(repository);
    }
}
