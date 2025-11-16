package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargingStationRepository extends JpaRepository<ChargingStation,Long> {
}
