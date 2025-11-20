package com.laipe.electricitybusiness.dto.chargingstations;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateChargingStationDTO {
    @NotBlank
    @Size(min = 2, max = 200)
    @JsonProperty("name")
    private String name;

    @NotNull
    @Digits(integer = 2, fraction = 8)
    @JsonProperty("latitude")
    private BigDecimal latitude;

    @NotNull
    @Digits(integer = 2, fraction = 8)
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
}
