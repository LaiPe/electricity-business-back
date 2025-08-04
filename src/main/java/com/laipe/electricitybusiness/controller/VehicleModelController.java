package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.controller.generic.GenericReadController;
import com.laipe.electricitybusiness.dto.NoMapper;
import com.laipe.electricitybusiness.model.VehicleModel;
import com.laipe.electricitybusiness.service.VehicleModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/vehicles/models")
public class VehicleModelController {

    private final GenericReadController<VehicleModel, VehicleModel, String> readController;

    public VehicleModelController(
            VehicleModelService service
    ) {
        this.readController = new GenericReadController<>(
                service,
                new NoMapper<>(),
                VehicleModel.class
        ){};
    }

    @GetMapping
    public ResponseEntity<List<VehicleModel>> getAll() {
        return readController.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleModel> getById(@PathVariable String id) {
        return readController.getById(id);
    }
}
