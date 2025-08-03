package com.laipe.electricitybusiness.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostVehicleDTO {
    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("owner_id")
    private Long ownerId;

    @JsonProperty("vehicle_model_id")
    private String vehicleModelId;
}
