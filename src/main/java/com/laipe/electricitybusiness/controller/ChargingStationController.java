package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.chargingstations.*;
import com.laipe.electricitybusiness.model.ChargingStation;
import com.laipe.electricitybusiness.service.ChargingStationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
@Slf4j
public class ChargingStationController {

    private final ChargingStationService chargingStationService;

    private final GetChargingStationMapper getChargingStationMapper;
    private final PostChargingStationMapper postChargingStationMapper;
    private final UpdateChargingStationMapper updateChargingStationMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isOwnerOfPlace(#dto.placeId)")
    public ResponseEntity<GetChargingStationDTO> create(@RequestBody @Valid PostChargingStationDTO dto) {
        return ResponseEntity.ok(
                getChargingStationMapper.toDto(
                        chargingStationService.create(
                                postChargingStationMapper.toEntity(dto)
                        )
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isOwnerOfChargingStation(#id)")
    public ResponseEntity<GetChargingStationDTO> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateChargingStationDTO dto
    ) {
        return chargingStationService.update(updateChargingStationMapper.toEntity(dto), id)
                .map(getChargingStationMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, ChargingStation.class)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetChargingStationDTO> getById(@PathVariable("id") Long id) {
        return chargingStationService.getById(id)
                .map(getChargingStationMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, ChargingStation.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isOwnerOfChargingStation(#id)")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        return chargingStationService.deleteById(id)
                .map(ignored -> ResponseEntity.noContent().build())
                .orElseThrow(() -> new ResourceNotFoundException(id, ChargingStation.class));

    }

}
