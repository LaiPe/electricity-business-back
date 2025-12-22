package com.laipe.electricitybusiness.dto.booking;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GetBookingAsStationOwnerMapper extends GenericDTOMapper<Booking, GetBookingAsStationOwnerDTO> {
    @Override
    @Mapping(target = "vehicle.vehicleModel.id", source = "vehicle.modelId")
    GetBookingAsStationOwnerDTO toDto(Booking entity);
}
