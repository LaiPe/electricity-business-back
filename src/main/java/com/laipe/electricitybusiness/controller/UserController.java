package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.dto.UserDTO;
import com.laipe.electricitybusiness.model.User;
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
    public ResponseEntity<List<UserDTO>> getAll() {
        List<UserDTO> dto = service.getAll()
                .stream()
                .map(mapper::entityToDto)
                .toList();

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody @Valid User entity) {
        return ResponseEntity.ok(mapper.entityToDto(service.create(entity)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(mapper::entityToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteById(@PathVariable Long id) {
        return service.deleteById(id)
                .map(mapper::entityToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateById(@PathVariable Long id, @RequestBody @Valid User newEntity) {
        return service.update(newEntity, id)
                .map(mapper::entityToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
}
