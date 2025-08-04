package com.laipe.electricitybusiness.controller.generic;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.service.generic.GenericService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@AllArgsConstructor
public abstract class GenericReadController<T, DTO, ID> {

    protected final GenericService<T, ID> service;
    protected final GenericDTOMapper<T, DTO> mapper;
    protected final Class<T> entityClass;

    public ResponseEntity<List<DTO>> getAll() {
        List<DTO> dto = service.getAll()
                .stream()
                .map(mapper::toDto)
                .toList();

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<DTO> getById(@PathVariable ID id) throws ResourceNotFoundException {
        return service.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, entityClass));
    }
}