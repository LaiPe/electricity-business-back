package com.laipe.electricitybusiness.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laipe.electricitybusiness.dto.user.PublicUserDTO;
import com.laipe.electricitybusiness.model.VehicleModel;
import lombok.Data;

@Data
public class GetVehiculeWithPublicUserDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("owner")
    private PublicUserDTO owner;

    @JsonProperty("vehicle_model")
    private VehicleModel vehicleModel;
}
