package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.model.VehicleModel;
import com.laipe.electricitybusiness.service.VehicleModelService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/vehicles/models")
@AllArgsConstructor
public class VehicleModelController {

    private final VehicleModelService service;

    @GetMapping
    public ResponseEntity<List<VehicleModel>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleModel> getById(@PathVariable String id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
