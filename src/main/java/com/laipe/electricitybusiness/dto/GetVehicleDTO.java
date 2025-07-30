package com.laipe.electricitybusiness.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laipe.electricitybusiness.model.VehicleModel;
import lombok.Data;

@Data
public class GetVehicleDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("owner_id")
    private Long ownerId;

    @JsonProperty("vehicle_model")
    private VehicleModel vehicleModel;
}
