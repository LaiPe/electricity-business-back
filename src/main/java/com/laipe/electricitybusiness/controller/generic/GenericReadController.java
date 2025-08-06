package com.laipe.electricitybusiness.controller.generic;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.service.generic.GenericService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@AllArgsConstructor
public abstract class GenericReadController<T, allDTO, byIdDTO, ID> {

    private final GenericService<T, ID> service;
    private final GenericDTOMapper<T, allDTO> allMapper;
    private final GenericDTOMapper<T, byIdDTO> byIdMapper;
    private final Class<T> entityClass;

    public ResponseEntity<List<allDTO>> getAll() {
        List<allDTO> dto = service.getAll()
                .stream()
                .map(allMapper::toDto)
                .toList();

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<byIdDTO> getById(@PathVariable ID id) throws ResourceNotFoundException {
        return service.getById(id)
                .map(byIdMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, entityClass));
    }
}