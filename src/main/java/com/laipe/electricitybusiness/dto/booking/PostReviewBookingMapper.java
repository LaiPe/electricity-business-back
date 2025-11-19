package com.laipe.electricitybusiness.dto.booking;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostReviewBookingMapper extends GenericDTOMapper<Booking, PostReviewBookingDTO> {
}
