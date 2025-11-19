package com.laipe.electricitybusiness.dto.place;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.Place;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostPlaceMapper extends GenericDTOMapper<Place, PostPlaceDTO> {
    @Override
    @Mapping(target = "chargingStations", ignore = true)
    Place toEntity(PostPlaceDTO dto);
}
