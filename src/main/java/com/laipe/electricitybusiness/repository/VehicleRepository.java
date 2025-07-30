package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
