package com.laipe.electricitybusiness.dto.chargingstations;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QueryFreeChargingStationDTO {

    @NotNull
    @FutureOrPresent
    @JsonProperty("search_start")
    private LocalDateTime searchStart;

    @NotNull
    @FutureOrPresent
    @JsonProperty("search_end")
    private LocalDateTime searchEnd;
}
