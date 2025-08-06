package com.laipe.electricitybusiness.controller.generic;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface GenericController<ID, CreateGetDTO, CreatePostDTO, ReadAllGetDTO, ReadByIdGetDTO, UpdateGetDTO, UpdatePostDTO, DeleteGetDTO> {

    ResponseEntity<CreateGetDTO> create(@RequestBody @Valid CreatePostDTO postDTO);

    ResponseEntity<List<ReadAllGetDTO>> getAll();
    ResponseEntity<ReadByIdGetDTO> getById(@PathVariable ID id) throws ResourceNotFoundException;

    ResponseEntity<UpdateGetDTO> updateById(@PathVariable ID id, @RequestBody @Valid UpdatePostDTO postDTO) throws ResourceNotFoundException;

    ResponseEntity<DeleteGetDTO> deleteById(@PathVariable ID id) throws ResourceNotFoundException;
}
