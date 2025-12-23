package com.laipe.electricitybusiness.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laipe.electricitybusiness.dto.vehicle.GetVehiculeWithPublicUserDTO;
import com.laipe.electricitybusiness.model.BookingState;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GetBookingAsStationOwnerDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("expected_end_date")
    private LocalDateTime expectedEndDate;

    @JsonProperty("actual_end_date")
    private LocalDateTime actualEndDate;

    @JsonProperty("final_price")
    private BigDecimal finalPrice;

    @JsonProperty("final_consumption_kwh")
    private BigDecimal finalConsumptionKwh;

    @JsonProperty("state")
    private BookingState state;

    @JsonProperty("review_grade")
    private Integer reviewGrade;

    @JsonProperty("review_comment")
    private String reviewComment;

    @JsonProperty("vehicle")
    private GetVehiculeWithPublicUserDTO vehicle;

    @JsonProperty("station")
    private ChargingStationInfo station;

    @Data
    public static class ChargingStationInfo {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("place")
        private PlaceInfo place;
    }

    @Data
    public static class PlaceInfo {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;
    }
}
