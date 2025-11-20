package com.laipe.electricitybusiness.dto.chargingstations;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("latitude")
    private BigDecimal latitude;

    @NotNull
    @Digits(integer = 3, fraction = 8)
    @Min(-180)
    @Max(180)
    @JsonProperty("longitude")
    private BigDecimal longitude;

    @NotNull
    @Min(0)
    @Max(1000)
    @JsonProperty("radius_in_km")
    private Integer radiusInKm;
}
