package com.laipe.electricitybusiness.util;

import com.laipe.electricitybusiness.dto.UserDTO;
import com.laipe.electricitybusiness.dto.VehicleDTO;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityDtoMapper {

    // USER
    @Mapping(target = "vehicles", source = "vehicles")
    UserDTO entityToDto(User user);

    @Mapping(target = "vehicles", ignore = true)
    User dtoToEntity(UserDTO userDTO);


    //VEHICLE
    @Mapping(target = "ownerId", source = "owner.id")
    VehicleDTO entityToDto(Vehicle vehicle);

    @Mapping(target = "owner", ignore = true)
    Vehicle dtoToEntity(VehicleDTO vehicleDTO);
}
