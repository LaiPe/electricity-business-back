package com.laipe.electricitybusiness.dto.booking;

import com.laipe.electricitybusiness.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GetReviewMapper {

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "rating", source = "reviewGrade")
    @Mapping(target = "comment", source = "reviewComment")
    @Mapping(target = "user", source = "vehicle.owner")
    GetReviewDTO toDto(Booking booking);
}

