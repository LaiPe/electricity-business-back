package com.laipe.electricitybusiness.dto.chargingstations;

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
    private String name;

    @NotNull
    @Digits(integer = 2, fraction = 8)
    private BigDecimal latitude;

    @NotNull
    @Digits(integer = 2, fraction = 8)
    private BigDecimal longitude;

    @NotNull
    @Digits(integer = 2, fraction = 2)
    private BigDecimal pricePerKwh;

    @NotNull
    @Digits(integer = 4, fraction = 2)
    private BigDecimal powerKw;

    private String instructions;
}
