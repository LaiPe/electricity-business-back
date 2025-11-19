package com.laipe.electricitybusiness.controller;

import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.dto.auth.StrictUserDTO;
import com.laipe.electricitybusiness.dto.booking.*;
import com.laipe.electricitybusiness.model.Booking;
import com.laipe.electricitybusiness.service.BookingService;
import com.laipe.electricitybusiness.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final SecurityUtil securityUtil;
    private final BookingService bookingService;

    private final PostBookingMapper postBookingMapper;
    private final GetBookingMapper getBookingMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isOwnerOfVehicle(#dto.vehicleId)")
    public ResponseEntity<GetBookingDTO> postBooking(@RequestBody PostBookingDTO dto) {
        return ResponseEntity.ok(
                getBookingMapper.toDto(
                        bookingService.create(
                                postBookingMapper.toEntity(dto)
                        )
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GetBookingDTO>> getAllBookings() {
        List<GetBookingDTO> bookings = bookingService.getAll()
                .stream()
                .map(getBookingMapper::toDto)
                .toList();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/as-vehicle-owner")
    public ResponseEntity<List<GetBookingDTO>> getBookingsAsVehicleOwner() {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();

        List<GetBookingDTO> bookings = bookingService.getAllByVehicleOwnerId(currentUser.getId())
                .stream()
                .map(getBookingMapper::toDto)
                .toList();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/as-station-owner")
    public ResponseEntity<List<GetBookingDTO>> getBookingsAsStationOwner() {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();

        List<GetBookingDTO> bookings = bookingService.getAllByStationOwnerId(currentUser.getId())
                .stream()
                .map(getBookingMapper::toDto)
                .toList();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isPartOfBooking(#id)")
    public ResponseEntity<GetBookingDTO> getBookingById(@PathVariable Long id) {
        return bookingService.getById(id)
                .map(getBookingMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Booking.class));
    }

    @PatchMapping("/{id}/accept")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isStationOwnerOfBooking(#id)")
    public ResponseEntity<GetBookingDTO> acceptBooking(@PathVariable Long id) {
        return bookingService.acceptBooking(id)
                .map(getBookingMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Booking.class));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isStationOwnerOfBooking(#id)")
    public ResponseEntity<GetBookingDTO> rejectBooking(@PathVariable Long id) {
        return bookingService.rejectBooking(id)
                .map(getBookingMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Booking.class));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isVehicleOwnerOfBooking(#id)")
    public ResponseEntity<GetBookingDTO> cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id)
                .map(getBookingMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Booking.class));
    }

    @PatchMapping("/{id}/start")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isVehicleOwnerOfBooking(#id)")
    public ResponseEntity<GetBookingDTO> startBooking(@PathVariable Long id) {
        return bookingService.startBooking(id)
                .map(getBookingMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Booking.class));
    }

    @PatchMapping("/{id}/end")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isVehicleOwnerOfBooking(#id)")
    public ResponseEntity<GetBookingDTO> endBooking(@PathVariable Long id) {
        return bookingService.endBooking(id)
                .map(getBookingMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Booking.class));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN') or @authorizeUtil.isVehicleOwnerOfBooking(#id)")
    public ResponseEntity<GetBookingDTO> reviewBooking(
            @PathVariable Long id,
            @RequestBody @Valid PostReviewBookingDTO dto
    ) {
        return bookingService.reviewBooking(id, dto.getReviewGrade(), dto.getReviewComment())
                .map(getBookingMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(id, Booking.class));
    }

}
