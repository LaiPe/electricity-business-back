package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.controller.handler.IntegrityConstraintViolationException;
import com.laipe.electricitybusiness.model.Vehicle;
import com.laipe.electricitybusiness.model.VehicleModel;
import com.laipe.electricitybusiness.repository.VehicleModelRepository;
import com.laipe.electricitybusiness.repository.VehicleRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class VehicleService extends GenericJPAService<Vehicle, Long> {

    private final VehicleModelRepository vehicleModelRepository;

    protected VehicleService(VehicleRepository vehicleRepository,  VehicleModelRepository vehicleModelRepository) {
        super(vehicleRepository);
        this.vehicleModelRepository = vehicleModelRepository;
    }

    @Override
    public Vehicle create(Vehicle entity) {
        vehicleModelRepository.findById(entity.getVehicleModelId())
            .orElseThrow(() -> new IntegrityConstraintViolationException("vehicleModelId", entity.getVehicleModelId(), VehicleModel.class));
        return super.create(entity);
    }

    @Override
    public Optional<Vehicle> update(Vehicle entity, Long id) {
        vehicleModelRepository.findById(entity.getVehicleModelId())
                .orElseThrow(() -> new IntegrityConstraintViolationException("vehicleModelId", entity.getVehicleModelId(), VehicleModel.class));
        return super.update(entity, id);
    }
}
