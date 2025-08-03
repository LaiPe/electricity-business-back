package com.laipe.electricitybusiness.util;

import com.laipe.electricitybusiness.dto.user.GetUserDTO;
import com.laipe.electricitybusiness.dto.user.PostUserDTO;
import com.laipe.electricitybusiness.dto.vehicle.GetVehicleDTO;
import com.laipe.electricitybusiness.dto.vehicle.PostVehicleDTO;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityDtoMapper {

    // USER
    GetUserDTO entityToDto(User user);

    User dtoToEntity(PostUserDTO postUserDTO);

    //VEHICLE
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "vehicleModel.id", source = "vehicleModelId") // Add this mapping
    GetVehicleDTO entityToDto(Vehicle vehicle);

    @Mapping(target = "owner.id", source = "ownerId")
    Vehicle dtoToEntity(PostVehicleDTO vehicleDTO);
}
