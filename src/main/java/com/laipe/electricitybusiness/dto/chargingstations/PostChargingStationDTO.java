package com.laipe.electricitybusiness.dto.chargingstations;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PostChargingStationDTO {

    @NotBlank
    @Size(min = 2, max = 200)
    private String name;

    @NotNull
    @Digits(integer = 2, fraction = 8)
    @Min(-90)
    @Max(90)
    private BigDecimal latitude;

    @NotNull
    @Digits(integer = 2, fraction = 8)
    @Min(-180)
    @Max(180)
    private BigDecimal longitude;

    @NotNull
    @Digits(integer = 2, fraction = 2)
    private BigDecimal pricePerKwh;

    @NotNull
    @Digits(integer = 4, fraction = 2)
    private BigDecimal powerKw;

    private String instructions;

    @NotNull
    @Min(1)
    private Long placeId;
}
