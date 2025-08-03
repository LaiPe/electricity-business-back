package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.dto.user.GetUserDTO;
import com.laipe.electricitybusiness.dto.user.PostUserDTO;
import com.laipe.electricitybusiness.service.UserService;
import com.laipe.electricitybusiness.util.EntityDtoMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService service;
    private final EntityDtoMapper mapper;

    @GetMapping
    public ResponseEntity<List<GetUserDTO>> getAll() {
        List<GetUserDTO> dto = service.getAll()
                .stream()
                .map(mapper::entityToDto)
                .toList();

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<GetUserDTO> create(@RequestBody @Valid PostUserDTO postUserDTO) {
        return ResponseEntity.ok(mapper.entityToDto(service.create(mapper.dtoToEntity(postUserDTO))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(mapper::entityToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GetUserDTO> deleteById(@PathVariable Long id) {
        return service.deleteById(id)
                .map(mapper::entityToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetUserDTO> updateById(@PathVariable Long id, @RequestBody @Valid PostUserDTO postUserDTO) {
        return service.update(mapper.dtoToEntity(postUserDTO), id)
                .map(mapper::entityToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
}
