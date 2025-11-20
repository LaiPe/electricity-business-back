package com.laipe.electricitybusiness.dto.chargingstations;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QueryFreeChargingStationDTO {

    @NotNull
    @FutureOrPresent
    LocalDateTime searchStart;

    @NotNull
    @FutureOrPresent
    LocalDateTime searchEnd;
}
