package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.user.*;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final GetFullUserMapper getFullUserMapper;
    private final GetUserMapper getUserMapper;
    private final PostUserMapper postUserMapper;

    @PostMapping
    public ResponseEntity<GetUserDTO> create(@RequestBody @Valid PostUserDTO dto) {
        return ResponseEntity.ok(getUserMapper.toDto(userService.create(postUserMapper.toEntity(dto))));
    }

    @GetMapping
    public ResponseEntity<List<GetUserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAll()
                .stream()
                .map(getUserMapper::toDto)
                .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetFullUserDTO> getById(@PathVariable @Min(1) Long id) throws ResourceNotFoundException {
        return userService.getById(id)
                .map(getFullUserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, User.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetFullUserDTO> updateById(@PathVariable @Min(1) Long id, @RequestBody @Valid PostUserDTO postDTO) {
        return userService.update(postUserMapper.toEntity(postDTO), id)
                .map(getFullUserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, User.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GetUserDTO> deleteById(@PathVariable @Min(1) Long id) {
        return userService.deleteById(id)
                .map(getUserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, User.class));
    }
}
