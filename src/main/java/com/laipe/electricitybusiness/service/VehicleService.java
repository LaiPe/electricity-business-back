package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.controller.handler.IntegrityConstraintViolationException;
import com.laipe.electricitybusiness.model.Vehicle;
import com.laipe.electricitybusiness.model.VehicleModel;
import com.laipe.electricitybusiness.repository.VehicleModelRepository;
import com.laipe.electricitybusiness.repository.VehicleRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleService extends GenericJPAService<Vehicle, Long> {

    private final VehicleRepository vehicleRepository;
    private final VehicleModelRepository vehicleModelRepository;

    protected VehicleService(VehicleRepository vehicleRepository,  VehicleModelRepository vehicleModelRepository) {
        super(vehicleRepository);
        this.vehicleRepository = vehicleRepository;
        this.vehicleModelRepository = vehicleModelRepository;
    }

    @Override
    public Vehicle create(Vehicle entity) {
        vehicleModelRepository.findById(entity.getModelId())
            .orElseThrow(() -> new IntegrityConstraintViolationException("vehicleModelId", entity.getModelId(), VehicleModel.class));
        return super.create(entity);
    }

    @Override
    public Optional<Vehicle> update(Vehicle entity, Long id) {
        vehicleModelRepository.findById(entity.getModelId())
                .orElseThrow(() -> new IntegrityConstraintViolationException("vehicleModelId", entity.getModelId(), VehicleModel.class));
        return super.update(entity, id);
    }

    public List<Vehicle> getAllByOwnerId(Long ownerId) {
        return vehicleRepository.findByOwnerId(ownerId);
    }
}
