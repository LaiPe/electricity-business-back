package com.laipe.electricitybusiness.dto.chargingstations;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.dto.booking.GetReviewDTO;
import com.laipe.electricitybusiness.dto.booking.GetReviewMapper;
import com.laipe.electricitybusiness.model.Booking;
import com.laipe.electricitybusiness.model.ChargingStation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {GetReviewMapper.class})
public abstract class GetChargingStationWithReviewsMapper implements GenericDTOMapper<ChargingStation, GetChargingStationWithReviewsDTO> {

    @Autowired
    protected GetReviewMapper getReviewMapper;

    @Override
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviews", expression = "java(filterBookingsWithReviews(entity.getBookings()))")
    public abstract GetChargingStationWithReviewsDTO toDto(ChargingStation entity);

    protected List<GetReviewDTO> filterBookingsWithReviews(List<Booking> bookings) {
        if (bookings == null) {
            return List.of();
        }
        return bookings.stream()
                .filter(booking -> booking.getReviewGrade() != null)
                .map(getReviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @AfterMapping
    protected void calculateAverageRating(ChargingStation entity, @MappingTarget GetChargingStationWithReviewsDTO dto) {
        if (entity.getBookings() != null && !entity.getBookings().isEmpty()) {
            List<Integer> ratings = entity.getBookings().stream()
                    .map(Booking::getReviewGrade)
                    .filter(Objects::nonNull)
                    .toList();

            if (!ratings.isEmpty()) {
                double average = ratings.stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0);
                dto.setAverageRating((int) Math.round(average));
            } else {
                dto.setAverageRating(0);
            }
        }
    }
}
