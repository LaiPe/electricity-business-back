package com.laipe.electricitybusiness.dto.chargingstations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laipe.electricitybusiness.dto.booking.GetReviewDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GetChargingStationWithReviewsDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("latitude")
    private BigDecimal latitude;

    @JsonProperty("longitude")
    private BigDecimal longitude;

    @JsonProperty("price_per_kwh")
    private BigDecimal pricePerKwh;

    @JsonProperty("power_kw")
    private BigDecimal powerKw;

    @JsonProperty("instructions")
    private String instructions;

    @JsonProperty("average_rating")
    private Integer averageRating;

    @JsonProperty("ratings")
    private List<GetReviewDTO> reviews;
}
