package com.laipe.electricitybusiness.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostVehicleDTO {
    @NotBlank
    @Size(min = 2, max = 15)
    @JsonProperty("registration_number")
    private String registrationNumber;

    @NotNull
    @JsonProperty("owner_id")
    private Long ownerId;

    @NotBlank
    @JsonProperty("vehicle_model_id")
    private String vehicleModelId;
}