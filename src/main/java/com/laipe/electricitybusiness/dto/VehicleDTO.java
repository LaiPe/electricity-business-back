package com.laipe.electricitybusiness.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VehicleDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("owner_id")
    private Long ownerId;

    @JsonProperty("vehicle_model_id")
    private String vehicleModelId;
}
