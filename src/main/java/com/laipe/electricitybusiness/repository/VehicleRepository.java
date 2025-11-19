package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByOwnerId(Long ownerId);
}
