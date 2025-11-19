package com.laipe.electricitybusiness.dto.booking;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GetBookingMapper extends GenericDTOMapper<Booking, GetBookingDTO> {
    @Override
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "stationId", source = "station.id")
    GetBookingDTO toDto(Booking entity);
}
