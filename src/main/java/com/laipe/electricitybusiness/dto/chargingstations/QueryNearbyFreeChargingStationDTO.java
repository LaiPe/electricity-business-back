package com.laipe.electricitybusiness.dto.chargingstations;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @NotNull
    @FutureOrPresent
    @JsonProperty("search_start")
    private LocalDateTime searchStart;

    @NotNull
    @FutureOrPresent
    @JsonProperty("search_end")
    private LocalDateTime searchEnd;
}
