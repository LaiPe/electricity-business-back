package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.ChargingStation;
import com.laipe.electricitybusiness.repository.ChargingStationRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChargingStationService extends GenericJPAService<ChargingStation, Long> {
    public ChargingStationService(ChargingStationRepository repository) {
        super(repository);
    }
}
