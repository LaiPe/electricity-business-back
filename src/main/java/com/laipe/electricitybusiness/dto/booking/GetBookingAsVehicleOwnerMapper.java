package com.laipe.electricitybusiness.dto.booking;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GetBookingAsVehicleOwnerMapper extends GenericDTOMapper<Booking, GetBookingAsVehicleOwnerDTO> {
    @Override
    @Mapping(target="station.owner", source = "station.place.owner")
    @Mapping(target = "vehicle.vehicleModel.id", source = "vehicle.modelId")
    GetBookingAsVehicleOwnerDTO toDto(Booking entity);
}
