package com.laipe.electricitybusiness.dto.booking;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostBookingMapper extends GenericDTOMapper<Booking, PostBookingDTO> {
    @Override
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "station.id", source = "stationId")
    Booking toEntity(PostBookingDTO dto);
}
