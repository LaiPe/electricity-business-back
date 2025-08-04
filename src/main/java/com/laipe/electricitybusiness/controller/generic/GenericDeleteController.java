package com.laipe.electricitybusiness.controller.generic;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.service.generic.GenericService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@AllArgsConstructor
public abstract class GenericDeleteController<T, GetDTO, ID> {

    protected final GenericService<T, ID> service;
    protected final GenericDTOMapper<T, GetDTO> mapper;
    protected final Class<T> entityClass;

    public ResponseEntity<GetDTO> deleteById(@PathVariable ID id) {
        return service.deleteById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, entityClass));
    }
}
