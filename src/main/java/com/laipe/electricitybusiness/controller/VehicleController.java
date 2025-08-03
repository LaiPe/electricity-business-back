package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.dto.vehicle.GetVehicleDTO;
import com.laipe.electricitybusiness.dto.vehicle.PostVehicleDTO;
import com.laipe.electricitybusiness.model.Vehicle;
import com.laipe.electricitybusiness.service.VehicleModelService;
import com.laipe.electricitybusiness.service.VehicleService;
import com.laipe.electricitybusiness.util.EntityDtoMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/vehicles")
@AllArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleModelService vehicleModelService;
    private final EntityDtoMapper mapper;

    @GetMapping
    public ResponseEntity<List<GetVehicleDTO>> getAll() {
        List<GetVehicleDTO> dto = vehicleService.getAll()
                .stream()
                .map(mapper::entityToDto)
                .map(vehicleDto -> {
                    vehicleModelService.getById(vehicleDto.getVehicleModel().getId())
                            .ifPresent(vehicleDto::setVehicleModel);
                    return vehicleDto;
                })
                .toList();

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<GetVehicleDTO> create(@RequestBody PostVehicleDTO inputDto) {
        Vehicle entity = mapper.dtoToEntity(inputDto);
        GetVehicleDTO dto = mapper.entityToDto(vehicleService.create(entity));
        vehicleModelService.getById(dto.getVehicleModel().getId()).ifPresent(dto::setVehicleModel);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetVehicleDTO> getById(@PathVariable Long id) {
        return vehicleService.getById(id)
                .map(mapper::entityToDto)
                .map(vehicleDto -> {
                    vehicleModelService.getById(vehicleDto.getVehicleModel().getId())
                            .ifPresent(vehicleDto::setVehicleModel);
                    return vehicleDto;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GetVehicleDTO> deleteById(@PathVariable Long id) {
        return vehicleService.deleteById(id)
                .map(mapper::entityToDto)
                .map(vehicleDto -> {
                    vehicleModelService.getById(vehicleDto.getVehicleModel().getId())
                            .ifPresent(vehicleDto::setVehicleModel);
                    return vehicleDto;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetVehicleDTO> updateById(@PathVariable Long id, @RequestBody PostVehicleDTO inputDto) {
        return vehicleService.update(mapper.dtoToEntity(inputDto), id)
                .map(mapper::entityToDto)
                .map(vehicleDto -> {
                    vehicleModelService.getById(vehicleDto.getVehicleModel().getId())
                            .ifPresent(vehicleDto::setVehicleModel);
                    return vehicleDto;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
