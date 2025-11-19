package com.laipe.electricitybusiness.utils;

import com.laipe.electricitybusiness.dto.auth.StrictUserDTO;
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

    public boolean isOwnerOfVehicle(Long vehicleId) {
        StrictUserDTO currentUser = securityUtil.getCurrentStrictUserFromAuthentification();
        if (currentUser == null) {
            return false;
        }
        return vehicleService.getById(vehicleId)
                .map(vehicle -> vehicle.getOwner().getId().equals(currentUser.getId()))
                .orElse(false);
    }
}
