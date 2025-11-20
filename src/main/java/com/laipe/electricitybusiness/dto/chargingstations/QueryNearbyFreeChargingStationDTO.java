package com.laipe.electricitybusiness.dto.chargingstations;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class QueryNearbyFreeChargingStationDTO {
    @NotNull
    @Digits(integer = 3, fraction = 8)
    @Min(-90)
    @Max(90)
    private BigDecimal latitude;

    @NotNull
    @Digits(integer = 3, fraction = 8)
    @Min(-180)
    @Max(180)
    private BigDecimal longitude;

    @NotNull
    @Min(0)
    @Max(1000)
    private Integer radiusInKm;

    @NotNull
    @FutureOrPresent
    LocalDateTime searchStart;

    @NotNull
    @FutureOrPresent
    LocalDateTime searchEnd;
}
