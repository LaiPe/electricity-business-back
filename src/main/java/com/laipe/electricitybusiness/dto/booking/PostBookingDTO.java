package com.laipe.electricitybusiness.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laipe.electricitybusiness.validation.StartDateBeforeEndDate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@StartDateBeforeEndDate(endDateField = "expectedEndDate")
public class PostBookingDTO {
    @NotNull
    @FutureOrPresent
    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @NotNull
    @Future
    @JsonProperty("expected_end_date")
    private LocalDateTime expectedEndDate;

    @NotNull
    @Min(1)
    @JsonProperty("vehicle_id")
    private Long vehicleId;

    @NotNull
    @Min(1)
    @JsonProperty("station_id")
    private Long stationId;
}
