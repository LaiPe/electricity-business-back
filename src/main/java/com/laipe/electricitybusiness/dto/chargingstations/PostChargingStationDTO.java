package com.laipe.electricitybusiness.dto.chargingstations;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PostChargingStationDTO {

    @NotBlank
    @Size(min = 2, max = 200)
    @JsonProperty("name")
    private String name;

    @NotNull
    @Digits(integer = 2, fraction = 8)
    @Min(-90)
    @Max(90)
    @JsonProperty("latitude")
    private BigDecimal latitude;

    @NotNull
    @Digits(integer = 2, fraction = 8)
    @Min(-180)
    @Max(180)
    @JsonProperty("longitude")
    private BigDecimal longitude;

    @NotNull
    @Digits(integer = 2, fraction = 2)
    @JsonProperty("price_per_kwh")
    private BigDecimal pricePerKwh;

    @NotNull
    @Digits(integer = 4, fraction = 2)
    @JsonProperty("power_kw")
    private BigDecimal powerKw;

    @JsonProperty("instructions")
    private String instructions;

    @NotNull
    @Min(1)
    @JsonProperty("place_id")
    private Long placeId;
}
