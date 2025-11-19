package com.laipe.electricitybusiness.dto.chargingstations;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QueryNearbyChargingStationDTO {
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
}
