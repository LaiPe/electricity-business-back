package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.Vehicle;
import com.laipe.electricitybusiness.repository.VehicleModelRepository;
import com.laipe.electricitybusiness.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class VehicleService extends GenericService<Vehicle, Long> {

    private final VehicleModelRepository vehicleModelRepository;

    protected VehicleService(VehicleRepository repository,  VehicleModelRepository vehicleModelRepository) {
        super(repository);
        this.vehicleModelRepository = vehicleModelRepository;
    }

    @Override
    public Vehicle create(Vehicle entity) {
        if (entity.getVehicleModelId() != null) {
            vehicleModelRepository.findById(entity.getVehicleModelId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle model not found"));
        }
        return super.create(entity);
    }

    @Override
    public Optional<Vehicle> update(Vehicle entity, Long id) {
        if (entity.getVehicleModelId() != null) {
            vehicleModelRepository.findById(entity.getVehicleModelId())
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle model not found"));
        }
        return super.update(entity, id);
    }
}
