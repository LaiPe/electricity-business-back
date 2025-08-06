package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.VehicleModel;
import com.laipe.electricitybusiness.repository.VehicleModelRepository;
import com.laipe.electricitybusiness.service.generic.GenericService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class VehicleModelService implements GenericService<VehicleModel, String> {

    private final VehicleModelRepository repository;

    @Override
    public Optional<VehicleModel> getById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<VehicleModel> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<VehicleModel> deleteById(String s) {
        return Optional.empty();
    }

    @Override
    public Optional<VehicleModel> update(VehicleModel newEntity, String s) {
        return Optional.empty();
    }

    @Override
    public VehicleModel create(VehicleModel entity) {
        return null;
    }
}
