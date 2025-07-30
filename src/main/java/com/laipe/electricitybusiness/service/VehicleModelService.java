package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.VehicleModel;
import com.laipe.electricitybusiness.repository.VehicleModelRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class VehicleModelService {

    private final VehicleModelRepository repository;

    public Optional<VehicleModel> getById(String id) {
        return repository.findById(id);
    }

    public List<VehicleModel> getAll() {
        return repository.findAll();
    }
}
