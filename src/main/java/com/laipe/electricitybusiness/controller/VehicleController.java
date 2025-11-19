package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.controller.handler.IntegrityConstraintViolationException;
import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.auth.StrictUserDTO;
import com.laipe.electricitybusiness.dto.vehicle.GetVehicleDTO;
import com.laipe.electricitybusiness.dto.vehicle.GetVehicleMapper;
import com.laipe.electricitybusiness.dto.vehicle.PostVehicleDTO;
import com.laipe.electricitybusiness.dto.vehicle.PostVehicleMapper;
import com.laipe.electricitybusiness.model.Vehicle;
import com.laipe.electricitybusiness.model.VehicleModel;
import com.laipe.electricitybusiness.service.VehicleModelService;
import com.laipe.electricitybusiness.service.VehicleService;
import com.laipe.electricitybusiness.utils.SecurityUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController()
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final SecurityUtil securityUtil;

    private final VehicleService vehicleService;
    private final VehicleModelService vehicleModelService;
    private final GetVehicleMapper getVehicleMapper;
    private final PostVehicleMapper postVehicleMapper;

    private GetVehicleDTO enrichVehicleWithModel(Vehicle vehicle) {
        GetVehicleDTO vehicleDTO = getVehicleMapper.toDto(vehicle);

        String modelId = vehicleDTO.getVehicleModel().getId();
        VehicleModel model = vehicleModelService.getById(modelId)
                .orElseThrow(() -> new IntegrityConstraintViolationException("vehicleModelId", modelId, VehicleModel.class));

        vehicleDTO.setVehicleModel(model);
        return vehicleDTO;
    }



    @PostMapping
    public ResponseEntity<GetVehicleDTO> create(@RequestBody @Valid PostVehicleDTO postVehicleDTO) {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();

        // Set the owner of the vehicle to the current user
        Vehicle vehicle = postVehicleMapper.toEntity(postVehicleDTO);
        vehicle.getOwner().setId(currentUser.getId());

        Vehicle createdVehicle = vehicleService.create(vehicle);
        GetVehicleDTO dto = enrichVehicleWithModel(createdVehicle);
        return ResponseEntity.ok(dto);
    }

    @GetMapping()
    public ResponseEntity<GetVehicleDTO> getByOwnerId() {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();
        Long ownerId = currentUser.getId();

        return vehicleService.getByOwnerId(ownerId)
                .map(this::enrichVehicleWithModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(ownerId, Vehicle.class));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GetVehicleDTO>> getAll() {
        List<GetVehicleDTO> dtos = vehicleService.getAll()
                .stream()
                .map(this::enrichVehicleWithModel)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isOwnerOfVehicle(#id)")
    public ResponseEntity<GetVehicleDTO> getById(@PathVariable @Min(1) Long id) {
        return vehicleService.getById(id)
                .map(this::enrichVehicleWithModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Vehicle.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isOwnerOfVehicle(#id)")
    public ResponseEntity<GetVehicleDTO> deleteById(@PathVariable @Min(1) Long id) {
        return vehicleService.deleteById(id)
                .map(this::enrichVehicleWithModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Vehicle.class));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isOwnerOfVehicle(#id)")
    public ResponseEntity<GetVehicleDTO> updateById(@PathVariable @Min(1) Long id, @RequestBody @Valid PostVehicleDTO inputDto) {
        return vehicleService.update(postVehicleMapper.toEntity(inputDto), id)
                .map(this::enrichVehicleWithModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Vehicle.class));
    }
}
