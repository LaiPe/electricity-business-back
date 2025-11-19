package com.laipe.electricitybusiness.dto.chargingstations;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.ChargingStation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostChargingStationMapper extends GenericDTOMapper<ChargingStation, PostChargingStationDTO> {
    @Override
    @Mapping(target = "place.id", source = "placeId")
    ChargingStation toEntity(PostChargingStationDTO dto);
}
