package com.laipe.electricitybusiness.utils;

import com.laipe.electricitybusiness.dto.auth.StrictUserDTO;
import com.laipe.electricitybusiness.service.BookingService;
import com.laipe.electricitybusiness.service.ChargingStationService;
import com.laipe.electricitybusiness.service.PlaceService;
import com.laipe.electricitybusiness.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizeUtil {
    private final SecurityUtil securityUtil;
    private final VehicleService vehicleService;
    private final PlaceService placeService;
    private final ChargingStationService chargingStationService;
    private final BookingService bookingService;

    public boolean isOwnerOfVehicle(Long vehicleId) {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();
        if (currentUser == null) {
            return false;
        }
        return vehicleService.getById(vehicleId)
                .map(vehicle -> vehicle.getOwner().getId().equals(currentUser.getId()))
                .orElse(false);
    }

    public boolean isOwnerOfPlace(Long placeId) {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();
        if (currentUser == null) {
            return false;
        }
        return placeService.getById(placeId)
                .map(place -> place.getOwner().getId().equals(currentUser.getId()))
                .orElse(false);
    }

    public boolean isOwnerOfChargingStation(Long stationId) {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();
        if (currentUser == null) {
            return false;
        }
        return chargingStationService.getById(stationId)
                .map(station -> isOwnerOfPlace(station.getPlace().getId()))
                .orElse(false);
    }

    public boolean isStationOwnerOfBooking(Long bookingId) {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();
        if (currentUser == null) {
            return false;
        }
        return bookingService.getById(bookingId)
                .map(booking -> isOwnerOfChargingStation(booking.getStation().getId()))
                .orElse(false);
    }

    public boolean isVehicleOwnerOfBooking(Long bookingId) {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();
        if (currentUser == null) {
            return false;
        }
        return bookingService.getById(bookingId)
                .map(booking -> isOwnerOfVehicle(booking.getVehicle().getId()))
                .orElse(false);
    }

    public boolean isPartOfBooking(Long bookingId) {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();
        if (currentUser == null) {
            return false;
        }
        return bookingService.getById(bookingId)
                .map(booking -> isOwnerOfChargingStation(booking.getStation().getId()) ||
                        isOwnerOfVehicle(booking.getVehicle().getId())
                )
                .orElse(false);
    }
}
