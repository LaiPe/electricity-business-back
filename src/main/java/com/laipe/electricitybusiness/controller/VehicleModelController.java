package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.model.VehicleModel;
import com.laipe.electricitybusiness.service.VehicleModelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController()
@Slf4j
@RequestMapping("/api/vehicles/models")
public class VehicleModelController {
    private final VehicleModelService service;

    @GetMapping
    public ResponseEntity<List<VehicleModel>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleModel> getById(@PathVariable String id) throws ResourceNotFoundException {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, VehicleModel.class));
    }

    @GetMapping("/search")
    public ResponseEntity<List<VehicleModel>> search(@RequestParam("q") String q) {
        log.info("Searching vehicle models with query: {}", q);
        return ResponseEntity.ok(service.search(q));
    }
}
