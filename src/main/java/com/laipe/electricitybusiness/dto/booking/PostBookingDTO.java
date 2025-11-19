package com.laipe.electricitybusiness.dto.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostBookingDTO {
    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime expectedEndDate;

    @NotNull
    @Min(1)
    private Long vehicleId;

    @NotNull
    @Min(1)
    private Long stationId;
}
