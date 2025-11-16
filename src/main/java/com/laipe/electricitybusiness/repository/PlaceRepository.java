package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place,Long> {
}
