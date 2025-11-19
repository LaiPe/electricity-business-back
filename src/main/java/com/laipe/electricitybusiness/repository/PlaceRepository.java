package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place,Long> {
    List<Place> findByOwnerId(Long ownerId);
}
