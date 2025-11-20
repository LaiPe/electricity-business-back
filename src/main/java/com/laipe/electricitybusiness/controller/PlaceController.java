package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.place.GetPlaceDTO;
import com.laipe.electricitybusiness.dto.place.GetPlaceMapper;
import com.laipe.electricitybusiness.dto.place.PostPlaceDTO;
import com.laipe.electricitybusiness.dto.place.PostPlaceMapper;
import com.laipe.electricitybusiness.model.Place;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.service.PlaceService;
import com.laipe.electricitybusiness.utils.SecurityUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
@Slf4j
public class PlaceController {
    private final SecurityUtil securityUtil;

    private final PlaceService placeService;

    private final PostPlaceMapper postPlaceMapper;
    private final GetPlaceMapper getPlaceMapper;

    @PostMapping
    public ResponseEntity<GetPlaceDTO> create(@RequestBody @Valid PostPlaceDTO postPlaceDTO) {
        // Récupérer l'id de l'utilisateur courant depuis le contexte de sécurité
        Long userId = securityUtil.getUserIdFromAuthentification();

        Place place = postPlaceMapper.toEntity(postPlaceDTO);
        place.setOwner(new User());
        place.getOwner().setId(userId);

        log.info("Creating place {}", place);

        return ResponseEntity.ok(getPlaceMapper.toDto(placeService.create(place)));
    }

    @GetMapping
    public ResponseEntity<List<GetPlaceDTO>> getAllByOwnerId() {
        // Récupérer l'id de l'utilisateur courant depuis le contexte de sécurité
        Long userId = securityUtil.getUserIdFromAuthentification();

        List<GetPlaceDTO> places = placeService.getAllByOwnerId(userId)
                .stream()
                .map(getPlaceMapper::toDto)
                .toList();

        return ResponseEntity.ok(places);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GetPlaceDTO>> getAll() {
        List<GetPlaceDTO> places = placeService.getAll()
                .stream()
                .map(getPlaceMapper::toDto)
                .toList();

        return ResponseEntity.ok(places);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isOwnerOfPlace(#id)")
    public ResponseEntity<GetPlaceDTO> getById(@PathVariable @Min(1) Long id) throws ResourceNotFoundException {
        return placeService.getById(id)
                .map(getPlaceMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Place.class));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isOwnerOfPlace(#id)")
    public ResponseEntity<GetPlaceDTO> update(@PathVariable @Min(1) Long id, @RequestBody @Valid PostPlaceDTO postPlaceDTO) {
        return placeService.update(postPlaceMapper.toEntity(postPlaceDTO), id)
                .map(getPlaceMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Place.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isOwnerOfPlace(#id)")
    public ResponseEntity<Object> deleteById(@PathVariable @Min(1) Long id) {
        return placeService.deleteById(id)
                .map(place -> ResponseEntity.noContent().build())
                .orElseThrow(() -> new ResourceNotFoundException(id, Place.class));
    }
}
