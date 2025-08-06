package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.controller.generic.GenericCreateController;
import com.laipe.electricitybusiness.controller.handler.IntegrityConstraintViolationException;
import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.vehicle.GetVehicleDTO;
import com.laipe.electricitybusiness.dto.vehicle.GetVehicleMapper;
import com.laipe.electricitybusiness.dto.vehicle.PostVehicleDTO;
import com.laipe.electricitybusiness.dto.vehicle.PostVehicleMapper;
import com.laipe.electricitybusiness.model.Vehicle;
import com.laipe.electricitybusiness.model.VehicleModel;
import com.laipe.electricitybusiness.service.VehicleModelService;
import com.laipe.electricitybusiness.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/vehicles")
public class VehicleController {

    private final GenericCreateController<Vehicle, GetVehicleDTO, PostVehicleDTO,Long> createController;

    private final VehicleService vehicleService;
    private final VehicleModelService vehicleModelService;
    private final GetVehicleMapper getVehicleMapper;
    private final PostVehicleMapper postVehicleMapper;

    public VehicleController(
            VehicleService vehicleService,
            VehicleModelService vehicleModelService,
            GetVehicleMapper getVehicleMapper,
            PostVehicleMapper postVehicleMapper
    ) {
        this.createController = new GenericCreateController<>(
                vehicleService,
                getVehicleMapper,
                postVehicleMapper
        ){};


        this.vehicleService = vehicleService;
        this.vehicleModelService = vehicleModelService;
        this.getVehicleMapper = getVehicleMapper;
        this.postVehicleMapper = postVehicleMapper;
    }


    @PostMapping
    public ResponseEntity<GetVehicleDTO> create(@RequestBody @Valid PostVehicleDTO postVehicleDTO) {
        return createController.create(postVehicleDTO);
    }

    private GetVehicleDTO enrichVehicleWithModel(Vehicle vehicle) {
        GetVehicleDTO vehicleDTO = getVehicleMapper.toDto(vehicle);

        String modelId = vehicleDTO.getVehicleModel().getId();
        VehicleModel model = vehicleModelService.getById(modelId)
                .orElseThrow(() -> new IntegrityConstraintViolationException("vehicleModelId", modelId, VehicleModel.class));

        vehicleDTO.setVehicleModel(model);
        return vehicleDTO;
    }

    @GetMapping
    public ResponseEntity<List<GetVehicleDTO>> getAll() {
        List<GetVehicleDTO> dtos = vehicleService.getAll()
                .stream()
                .map(this::enrichVehicleWithModel)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetVehicleDTO> getById(@PathVariable Long id) {
        return vehicleService.getById(id)
                .map(this::enrichVehicleWithModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Vehicle.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GetVehicleDTO> deleteById(@PathVariable Long id) {
        return vehicleService.deleteById(id)
                .map(this::enrichVehicleWithModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Vehicle.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetVehicleDTO> updateById(@PathVariable Long id, @RequestBody @Valid PostVehicleDTO inputDto) {
        return vehicleService.update(postVehicleMapper.toEntity(inputDto), id)
                .map(this::enrichVehicleWithModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Vehicle.class));
    }
}
