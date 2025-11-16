package com.laipe.electricitybusiness.dto.vehicle;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GetVehicleMapper extends GenericDTOMapper<Vehicle, GetVehicleDTO> {
    @Override
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "vehicleModel.id", source = "modelId")
    GetVehicleDTO toDto(Vehicle vehicle);
}
