package com.laipe.electricitybusiness.controller.generic;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.service.generic.GenericService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
public abstract class GenericUpdateController<T, GetDTO, PostDTO, ID> {

    protected final GenericService<T, ID> service;
    protected final GenericDTOMapper<T, GetDTO> getMapper;
    protected final GenericDTOMapper<T, PostDTO> postMapper;
    protected final Class<T> entityClass;

    public ResponseEntity<GetDTO> updateById(@PathVariable ID id, @RequestBody @Valid PostDTO postDTO) throws ResourceNotFoundException {
        return service.update(postMapper.toEntity(postDTO), id)
                .map(getMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, entityClass));
    }
}