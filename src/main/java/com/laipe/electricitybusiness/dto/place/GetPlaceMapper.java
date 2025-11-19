package com.laipe.electricitybusiness.dto.place;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.Place;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GetPlaceMapper extends GenericDTOMapper<Place, GetPlaceDTO> {
    @Override
    @Mapping(source = "owner.id", target = "ownerId")
    GetPlaceDTO toDto(Place place);
}
