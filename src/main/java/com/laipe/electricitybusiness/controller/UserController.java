package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.dto.UserDTO;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.service.UserService;
import com.laipe.electricitybusiness.utils.ModelUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        List<UserDTO> dto = service.getAll()
                .stream()
                .map(ModelUtils::<User, UserDTO>toDTO)
                .toList();

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody @Valid User entity) {
        return ResponseEntity.ok(ModelUtils.toDTO(service.create(entity)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ModelUtils::<User, UserDTO>toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteById(@PathVariable Long id) {
        return service.deleteById(id)
                .map(ModelUtils::<User, UserDTO>toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateById(@PathVariable Long id, @RequestBody @Valid User newEntity) {
        return service.update(newEntity, id)
                .map(ModelUtils::<User, UserDTO>toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
}
