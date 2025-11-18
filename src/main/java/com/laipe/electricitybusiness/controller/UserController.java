package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.auth.StrictUserDTO;
import com.laipe.electricitybusiness.dto.user.*;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.service.UserService;
import com.laipe.electricitybusiness.utils.SecurityUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController()
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final SecurityUtil securityUtil;

    private final GetUserMapper getUserMapper;
    private final PostUserMapper postUserMapper;
    private final UpdateUserMapper updateUserMapper;
    private final UpdateUsernameMapper updateUsernameMapper;
    private final UpdatePasswordMapper updatePasswordMapper;
    private final UpdateEmailMapper updateEmailMapper;

    @PostMapping

    public ResponseEntity<GetUserDTO> create(@RequestBody @Valid PostUserDTO dto) {
        return ResponseEntity.ok(getUserMapper.toDto(userService.create(postUserMapper.toEntity(dto))));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GetUserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAll()
                .stream()
                .map(getUserMapper::toDto)
                .toList()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GetUserDTO> getById(@PathVariable @Min(1) Long id) throws ResourceNotFoundException {
        return userService.getById(id)
                .map(getUserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, User.class));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GetUserDTO> updateById(@PathVariable @Min(1) Long id, @RequestBody @Valid PostUserDTO postDTO) {
        return userService.update(postUserMapper.toEntity(postDTO), id)
                .map(getUserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, User.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GetUserDTO> deleteById(@PathVariable @Min(1) Long id) {
        return userService.deleteById(id)
                .map(getUserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, User.class));
    }

    @GetMapping("/me")
    public ResponseEntity<GetUserDTO> me() {
        StrictUserDTO dto = securityUtil.getCurrentStrictUserFromAuthentification();

        return ResponseEntity.ok(userService.getById(dto.getId())
                .map(getUserMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
        );
    }

    @PutMapping("/me")
    public ResponseEntity<GetUserDTO> updateDetails(@RequestBody @Valid UpdateUserDTO updateDTO) {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();

        return userService.update(updateUserMapper.toEntity(updateDTO), currentUser.getId())
                .map(getUserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @PutMapping("/me/username")
    public ResponseEntity<GetUserDTO> updateUsername(@RequestBody @Valid UpdateUsernameDTO updateDTO) {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();

        return userService.update(updateUsernameMapper.toEntity(updateDTO), currentUser.getId())
                .map(getUserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @PutMapping("/me/password")
    public ResponseEntity<GetUserDTO> updatePassword(@RequestBody @Valid UpdatePasswordDTO updateDTO) {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();

        // Hashage du password assuré par le service lors de la mise à jour
        return userService.update(updatePasswordMapper.toEntity(updateDTO), currentUser.getId())
                .map(getUserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @PutMapping("/me/email")
    public ResponseEntity<GetUserDTO> updateEmail(@RequestBody @Valid UpdateEmailDTO updateDTO) {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();

        return userService.update(updateEmailMapper.toEntity(updateDTO), currentUser.getId())
                .map(getUserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
