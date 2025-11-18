package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.ChargingStation;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import org.springframework.data.jpa.repository.JpaRepository;

public class ChargingStationService extends GenericJPAService<ChargingStation, Long> {
    public ChargingStationService(JpaRepository<ChargingStation, Long> repository) {
        super(repository);
    }
}
