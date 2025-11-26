package com.laipe.electricitybusiness.dto.vehicle;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostVehicleMapper extends GenericDTOMapper<Vehicle, PostVehicleDTO> {

    @Override
    @Mapping(target="modelId", source="vehicleModelId")
    Vehicle toEntity(PostVehicleDTO dto);
}
