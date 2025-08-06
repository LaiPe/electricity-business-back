package com.laipe.electricitybusiness.controller.generic;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.service.generic.GenericService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
public abstract class GenericCreateController<T, GetDTO, PostDTO, ID> {

    protected final GenericService<T, ID> service;
    protected final GenericDTOMapper<T, GetDTO> getMapper;
    protected final GenericDTOMapper<T, PostDTO> postMapper;

    public ResponseEntity<GetDTO> create(@RequestBody @Valid PostDTO postDTO) {
        return ResponseEntity.ok(getMapper.toDto(service.create(postMapper.toEntity(postDTO))));
    }
}