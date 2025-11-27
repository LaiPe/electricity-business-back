package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChargingStationRepository extends JpaRepository<ChargingStation,Long> {

    @Query("SELECT cs FROM ChargingStation cs WHERE cs.deletedAt IS NULL")
    List<ChargingStation> findAllNotDeleted();

    @Query("SELECT cs FROM ChargingStation cs WHERE cs.deletedAt IS NOT NULL")
    List<ChargingStation> findAllDeleted();
}
